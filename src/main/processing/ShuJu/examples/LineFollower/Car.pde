final int ROAD_WIDTH = 4;
final int TURTLE_SIZE = 32;

class Car
{
  public int camera;
  public boolean die;
  public boolean hide;
  
  public Car()
  {
    SpriteSheet sp = spl.loadSpriteSheet("car.png", 64, 64);
    sprite = sp.getImage(0);
    explosion = sp.getAnimation(9, 90, 0);

    sensors[0] = new PVector(TURTLE_SIZE / 2, -ROAD_WIDTH);
    sensors[1] = new PVector(TURTLE_SIZE / 2, 0);
    sensors[2] = new PVector(TURTLE_SIZE / 2, ROAD_WIDTH);
  }

  public void spawn(PVector newLocation) {
    location = newLocation;
    speed = new PVector(-1.0, 0);
    die =  false;
    hide = false;
  }
  
  public void kill() {
    explosion.rewind();
    die = true;
    hide = true;
  }
  
  public int captureCamera(PImage image)
  {
    camera = 0;
    for(int i = 0 ; i < sensors.length; i++) {
      camera |= (readSensor(sensors[i], image) <= 100) ? (1 << i) : 0;
    }
    return camera;
  }
  
  public void turnAround() {
    speed.rotate(radians(180));
  }
  
  public void turnLeft() 
  {
    speed.rotate(-radians(2));
    location.add(speed.copy().mult(0.25));
  }
  
  public void turnRight() 
  {
    speed.rotate(radians(2));
    location.add(speed.copy().mult(0.25));
  }
  
  public void moveForward() 
  {
    location.add(speed);
  }
  
  public void draw(float zoomX, float zoomY)
  {
    if(location == null) {
      return;
    }
    
    pushMatrix();
    
    translate(location.x * zoomX, location.y * zoomY);
    rotate(speed.heading());
    
    if(die) {
      imageMode(CENTER);
      die = explosion.play(0, 0, TURTLE_SIZE * zoomY, TURTLE_SIZE * zoomY);
      imageMode(CORNER);
    }
    else if(!hide) {
      imageMode(CENTER);
      image(sprite, 0, 0, TURTLE_SIZE * zoomY, TURTLE_SIZE * zoomY);
      imageMode(CORNER);
  
      //fill(255, 0, 0);
      //for(int i = 0 ; i < sensors.length; i++) {
      //  ellipse(sensors[i].x * zoom, sensors[i].y * zoom, 4, 4);
      //}
    }
 
    popMatrix();
  }
  
  private int readSensor(PVector sensor, PImage image)
  {
    PVector a = sensor.copy();
    a.rotate(speed.heading());
    a.add(location);
    if(a.x < 0 || a.x >= image.width || a.y < 0 || a.y >= image.height) {
      return 255;
    }
    else {
      return int(brightness(image.pixels[int(a.y) * image.width + int(a.x)]));
    }
  }
  
  private PImage sprite;
  private SpriteAnimation explosion;
  private PVector location;
  private PVector speed;
  private PVector[] sensors = new PVector[3];
}
