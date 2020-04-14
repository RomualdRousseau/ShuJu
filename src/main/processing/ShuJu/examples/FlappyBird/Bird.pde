import com.github.romualdrousseau.shuju.columns.*;
import com.github.romualdrousseau.shuju.cv.*;
import com.github.romualdrousseau.shuju.cv.templatematching.*;
import com.github.romualdrousseau.shuju.*;
import com.github.romualdrousseau.shuju.genetic.*;
import com.github.romualdrousseau.shuju.json.jackson.*;
import com.github.romualdrousseau.shuju.json.processing.*;
import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.ml.naivebayes.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.qlearner.*;
import com.github.romualdrousseau.shuju.nlp.*;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.builder.*;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.json.*;
import com.github.romualdrousseau.shuju.ml.kmean.*;
import com.github.romualdrousseau.shuju.ml.nn.initializer.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.nn.regularizer.*;
import com.github.romualdrousseau.shuju.util.*;
import com.github.romualdrousseau.shuju.ml.nn.scheduler.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.nlp.impl.*;
import com.github.romualdrousseau.shuju.math.distribution.*;

GeneticPool BirdPool = new GeneticPool();

class Bird extends Entity implements Individual {
  Model brain;
  ArrayList<Particle> smoke = new ArrayList<Particle>();
  float altitude = 0;
  float brainFitness = -1;
  float bonus = 0;
  
  Bird() {
    super();

    this.brain = new Model();
    this.brain.add(new GeneticBuilder()
      .setInputUnits(5)
      .setUnits(8)
      .setMutationRate(MUTATION_RATE));
    this.brain.add(new ActivationBuilder()
      .setActivation(new Tanh()));  
    this.brain.add(new GeneticBuilder()
      .setUnits(2)
      .setMutationRate(MUTATION_RATE));
    this.brain.add(new ActivationBuilder()
      .setActivation(new Softmax()));

    this.position = new PVector(WIDTH / 4, floor(random(BIRD_MASS, HEIGHT - BIRD_MASS)));
    this.velocity = new PVector(0, 0);
    this.acceleration = new PVector(0, 0);
  }

  Bird(com.github.romualdrousseau.shuju.json.JSONArray jsonBrain) {
    super();

    this.brain = new Model();
    this.brain.add(new GeneticBuilder()
      .setInputUnits(5)
      .setUnits(8)
      .setMutationRate(MUTATION_RATE));
    this.brain.add(new ActivationBuilder()
      .setActivation(new Tanh()));  
    this.brain.add(new GeneticBuilder()
      .setUnits(2)
      .setMutationRate(MUTATION_RATE));
    this.brain.add(new ActivationBuilder()
      .setActivation(new Softmax()));
    this.brain.fromJSON(jsonBrain);

    this.position = new PVector(WIDTH / 4, floor(random(BIRD_MASS, HEIGHT - BIRD_MASS)));
    this.velocity = new PVector(0, 0);
    this.acceleration = new PVector(0, 0);
  }

  Bird(Bird parent) {
    super();

    this.brain = parent.brain.clone();

    this.position = new PVector(WIDTH / 4, floor(random(BIRD_MASS, HEIGHT - BIRD_MASS)));
    this.velocity = new PVector(0, 0);
    this.acceleration = new PVector(0, 0);
    this.brainFitness = -1;
  }

  float getFitness() {
    if (this.brainFitness == -1) {
      this.brainFitness = this.life* (0.95 + 0.05 * bonus); // Give a 5% bonus if the bird flies around the center of the pillars
    }
    return this.brainFitness;
  }

  void setFitness(float f) {
    this.brainFitness = f;
  }

  Bird clone() {
    return new Bird(this);
  }

  Bird mutate() {
    new OptimizerSgdBuilder().setLearningRate(MUTATION_RATE).build(this.brain).step();
    return this;
  }

  boolean isOffscreen() {
    return this.position.y < BIRD_MASS / 2 - 1;
  }

  boolean hit() {
    Pillar closest = this.lookat();
    if (closest == null) {
      return false;
    }

    float r = BIRD_MASS / 4; // Roughly the hit box, somehow generous. hey! I want meet my baby
    float d = closest.bottom.x - this.position.x; // Negative is important here because the position of the pillar is relative to the center of the bird

    if (d < r && d > -(PILLAR_SIZE + r)) {
      if (this.position.y < closest.bottom.y + r || this.position.y > (closest.top.y - r)) {
        return true;
      } else {
        altitude = 0.9 * altitude + 0.1 * abs(this.position.y - closest.bottom.y);
        bonus = 0.9 * bonus + 0.1 / (1.0 + abs(this.position.y - (closest.bottom.y + PILLAR_SPACING / 2)));
        return false;
      }
    } else {
      return false;
    }
  }

  boolean think() {
    if (mode == GameMode.INTERACTIVE) {
      return mousePressed || keyPressed && key == ' ';
    }

    Pillar closest = this.lookat();
    if (closest == null) {
      return false;
    }

    Tensor2D input = new Tensor2D(new float[] {
      this.position.y / HEIGHT, 
      this.velocity.y / BIRD_MAX_SPEED, 
      (closest.top.x - this.position.x) / WIDTH, 
      (closest.top.y - this.position.y) / HEIGHT, 
      (closest.bottom.y - this.position.y) / HEIGHT
      }, false);
    Tensor2D output = this.brain.model(input).detach();

    return output.get(0, 0) > output.get(1, 0);
  }

  void meet(Entity entity) {
    if (entity != null) {
      PVector target = new PVector(-TROPHEE_SIZE / 2, 0).add(entity.position.copy());
      PVector force = target.sub(this.position);
      force.add(this.velocity.copy().mult(-DRAG_COEF)); // Friction
      this.acceleration.add(force.div(BIRD_MASS));
    }
  }

  void fly() {
    PVector force = new PVector(0, BIRD_FLY_FORCE);
    this.acceleration.add(force.div(BIRD_MASS));
  }

  void gravity() {
    PVector force = new PVector(0, G * BIRD_MASS);
    this.acceleration.add(force.div(BIRD_MASS));
  }

  void limit() {
    if (this.position.y >= HEIGHT - BIRD_MASS / 2) {
      PVector force = new PVector(0, -this.velocity.mag() * 0.5);
      this.acceleration.mult(0.0).add(force);
    }
  }

  void constrainToScreen() {
    this.velocity.y = constrain(this.velocity.y, -BIRD_MAX_SPEED, BIRD_MAX_SPEED);
    this.position.y = constrain(this.position.y, BIRD_MASS / 2, HEIGHT - BIRD_MASS / 2);
  }

  Pillar lookat() {
    Pillar closest = null;
    for (int i = 0; i < pillars.size(); i++) {
      float d = pillars.get(i).bottom.x - this.position.x;
      if (d >= -(PILLAR_SIZE + BIRD_MASS / 4)) {
        closest = pillars.get(i);
        break;
      }
    }
    return closest;
  }

  void emitSmoke() {
    if (this.smoke.size() < 5 && this.velocity.heading() > radians(45)) {
      this.smoke.add(new Particle(this));
    }
    for (int i = smoke.size() - 1; i >= 0; i--) {
      Particle particle = smoke.get(i);
      particle.update();
      if (particle.life > SMOKE_LIFE) {
        particle.kill();
        smoke.remove(particle);
      }
    }
  }

  void render() {
    imageMode(CENTER);
    pushMatrix();
    translate(mapToScreenX(this.position.x), mapToScreenY(this.position.y));
    rotate(-constrain(this.velocity.heading(), radians(0), radians(45)));
    image(BIRD_SPRITE, 0, 0, scaleToScreenXY(BIRD_MASS), scaleToScreenY(BIRD_MASS));
    popMatrix();

    if (!this.alive) {
      int frame = floor(frameCount * frameRate / 500.0) % 4;
      if (frame == 0) {
        image(STAR_SPRITE, mapToScreenX(this.position.x + 32), mapToScreenY(this.position.y + 32), scaleToScreenXY(32), scaleToScreenY(32));
      }
      if (frame == 1) {
        image(STAR_SPRITE, mapToScreenX(this.position.x + 16), mapToScreenY(this.position.y - 16), scaleToScreenXY(48), scaleToScreenY(48));
      }
      if (frame == 2) {
        image(STAR_SPRITE, mapToScreenX(this.position.x - 16), mapToScreenY(this.position.y - 16), scaleToScreenXY(64), scaleToScreenY(64));
      }
      if (frame == 3) {
        image(STAR_SPRITE, mapToScreenX(this.position.x - 32), mapToScreenY(this.position.y + 32), scaleToScreenXY(48), scaleToScreenY(48));
      }
    } else {
      for (int i = 0; i < smoke.size(); i++) {
        Particle particle = smoke.get(i);
        particle.render();
      }
    }

    if (DEBUG) {
      fill(255, 128);
      strokeWeight(2);
      stroke(255, 0, 0);
      ellipse(mapToScreenX(this.position.x), mapToScreenY(this.position.y), scaleToScreenX(BIRD_MASS / 2), scaleToScreenY(BIRD_MASS / 2));
      stroke(0, 255, 0);
      ellipse(mapToScreenX(this.position.x), mapToScreenY(this.position.y), scaleToScreenX(BIRD_MASS), scaleToScreenY(BIRD_MASS));
    }
  }
}
