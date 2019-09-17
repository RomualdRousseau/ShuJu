import com.github.romualdrousseau.shuju.ml.qlearner.*;
import org.spritesheet.*;

SpriteSheetLibrary spl;
QLearner ql;
float confidence;

void setup() {
  //fullScreen(P2D);
  size(800, 800, P2D);
  textSize(16);
  noStroke();
  frameRate(30);

  spl = new SpriteSheetLibrary(this);
  QEnvironment env = new LineFollowerEnvImpl();
  QMatrix mat = new QMatrixNnImpl(env, 1, 1, 20);
  ql = new QLearner(env, 0.95, 0.2, mat);
  confidence = 0.05;
}

void draw() {
  if(ql.explore(confidence)) {
    confidence = min(confidence * 1.5, 0.8);
  }
  ql.draw();
  fill(255);
  text(String.format("Confidence: %.00f%%", confidence * 100), 0, 16);
}

void keyPressed() {
  if(key == '-' && confidence > 0) {
    confidence-=0.01;
  }
  else if(key == '+' && confidence < 1.0) {
    confidence+=0.01;
  }
  else if(key == 'r') {
    ql.reset();
    confidence = 0.05;
  }
}
