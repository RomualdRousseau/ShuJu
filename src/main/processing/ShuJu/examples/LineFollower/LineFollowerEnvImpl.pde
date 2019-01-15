final int ACTION_NONE          = 0;
final int ACTION_MOVE_LEFT     = 1;
final int ACTION_MOVE_RIGHT    = 2;
final int ACTION_MOVE_FORWARD  = 3;

final int CAMERA_BIT_COUNT  = 3;
final int CAMERA_BIT1       = 1;
final int CAMERA_BIT2       = 2;
final int CAMERA_BIT3       = 4;

class LineFollowerEnvImpl extends QEnvironment
{
  public LineFollowerEnvImpl() {
    super((int) pow(2, CAMERA_BIT_COUNT), 3, true);

    SpriteSheet sp = spl.loadSpriteSheet("field.png", 400, 400);
    field1 = sp.getImage(0);
    field2 = sp.getImage(1);
    
    car = new Car();
  }

  public boolean isWon() {
    return false;
  }
  
  public boolean isLost() {
    if(getReward() == -1) {
      car.kill();
      interactiveMode = true;
      return true;
    }
    else {
      return false;
    }
  }
  
  public int reset() {
    return 0;
  }
  
  public int getState() {
    return car.captureCamera(field1);
  }
  
  public double getReward() {
    if(car.camera == 0 || car.camera == CAMERA_BIT1 + CAMERA_BIT3) {
      return -1;
    }
    else if(car.camera == CAMERA_BIT2 || car.camera == CAMERA_BIT1 + CAMERA_BIT2) {
      return 1;
    }
    else {
      return 0;
    }
  }
  
  public boolean doAction(int a) {
    switch(a)
    {
      case ACTION_MOVE_LEFT:
        car.turnLeft();
        return true;
      case ACTION_MOVE_FORWARD:
        car.moveForward();
        return true;
      case ACTION_MOVE_RIGHT:
        car.turnRight();
        return true;
    }
    return false;
  }
  
  public void doInteractive() {
    //if(!car.die && mousePressed) {
      //car.spawn(new PVector(mouseX * field1.width / width, mouseY * field1.height / height));
    if(!car.die) {
      car.spawn(new PVector(304, 160));
      car.captureCamera(field1);
      interactiveMode = false;
    }
  }
  
  public void draw() {
    drawLayer0();
    drawLayer1();
    drawLayer2();
  }
  
  private void drawLayer0() {
    image(field1, 0, 0, width, height);
  }
  
  private void drawLayer1() {
    car.draw((float) width / field1.width, (float) height / field1.height);
  }
  
  private void drawLayer2() {
    image(field2, 0, 0, width, height);
    for(int i = 0; i < CAMERA_BIT_COUNT; i++) {
      fill(color(((car.camera & (1 << i)) > 0) ? 255 : 0, 0, 0));
      rect(width - i * 32 - 32, 0, 32, 32);
    }
  }
  
  private PImage field1;
  private PImage field2; 
  private Car car;
}