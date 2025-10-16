class Game_ {
  long frameCounter;
  GameState state;
  int score;
  int pillarInterval;
  int pillarCount;
  boolean isTraining;

  void startup(boolean firstRun) {
    this.state = GameState.INIT;
    this.score = 0;
    this.frameCounter = 0;
    this.pillarInterval = floor(PILLAR_INTERVAL * frameRate / PILLAR_SCROLLING_SPEED);
    this.pillarCount = 0;
    
    landscape = new Landscape();
    friend = new Friend();
    trophee = null;
    pillars = new ArrayList<Pillar>();
    
    birds = new ArrayList<Bird>();
    if (mode == GameMode.DEMO) {
      if (fileExistsInData("melody.json")) {
        com.github.romualdrousseau.shuju.json.JSONArray jsonBrain = JSON.loadJSONArray(getDataPath("melody.json"));
        birds.add(new Bird(jsonBrain));
        this.isTraining = false;
      } else if (firstRun) {
        for (int i = 0; i < BIRDS_COUNT; i++) {
          birds.add(new Bird());
        }
        this.isTraining = true;
      } else {
        BirdPool.sample();
        BirdPool.normalize();
        for (int i = 0; i < BIRDS_COUNT; i++) {
          birds.add((Bird) BirdPool.spawn());
        }
        this.isTraining = true;
      }
      BirdPool.newPool();
    } else {
      if (fileExistsInData("melody.json")) {
        com.github.romualdrousseau.shuju.json.JSONArray jsonBrain = JSON.loadJSONArray(getDataPath("melody.json"));
        birds.add(new Bird(jsonBrain));
      } else {
        birds.add(new Bird());
      }
      this.isTraining = false;
    }
  }
  
  void mainloop() {
    for (int i = 0; i < cycles * simulationSteps; i++) {
      this.runOnce();
    }
  }
  
  void runOnce() {
    final float dt = 1.0f / frameRate;
    
    landscape.update(dt);
    
    switch(state) {
    case INIT:
      if (isSpaceBarPressed(GameMode.INTERACTIVE)) {
        this.state = GameState.MAINLOOP;
      }
      break;
  
    case MAINLOOP:
      if(this.score >= (this.isTraining ? MAX_SCORE_TRAINING : MAX_SCORE)) {
        trophee = new Trophee();
        UI.show = false;
        this.state = GameState.GAMEWIN;
      } else {
        this.spawnNewPillar();

        friend.meet(null);
        friend.update(dt);
        friend.constrainToScreen();
        
        for (int i = pillars.size() - 1; i >= 0; i--) {
          Pillar pillar = pillars.get(i);
          pillar.update(dt);
          if(pillar.isOffView()) {
            if(isAudioPlayable()) {
              POINT_SOUND.play();
            }
            this.score++;
          }
          if (pillar.isOffscreen()) {
            pillars.remove(pillar);
          }
        }
    
        for (int i = birds.size() - 1; i >= 0; i--) {
          Bird bird = birds.get(i);
          if (bird.think()) {
            bird.fly();
          }
          bird.limit();
          bird.gravity();
          bird.update(dt);
          if (bird.isOffscreen() || bird.hit()) {
            if(isAudioPlayable()) {
              CRASH_SOUND.play();
            }
            bird.kill();
            if (mode == GameMode.DEMO) {
              BirdPool.addOne(bird);
              birds.remove(bird);
              if (birds.size() == 0) {
                this.state = GameState.GAMEOVER;
              }
            } else {
              this.state = GameState.GAMEOVER;
            }
          }
          bird.constrainToScreen();
          bird.emitSmoke(dt);
        }
      }
      break;
  
    case GAMEOVER:
      for (int i = birds.size() - 1; i >= 0; i--) {
        Bird bird = birds.get(i);
        bird.limit();
        bird.gravity();
        bird.update(dt);
        bird.constrainToScreen();
      }
      
      if (isSpaceBarPressed(GameMode.INTERACTIVE)) {
        this.startup(false);
      }
      break;
      
    case GAMEWIN:      
      for (int i = pillars.size() - 1; i >= 0; i--) {
        Pillar pillar = pillars.get(i);
        pillar.update(dt);
        if (pillar.isOffscreen()) {
          pillars.remove(pillar);
          landscape.stop();
        }
      }

      friend.meet(trophee);
      friend.update(dt);
      friend.hover_over();
      friend.constrainToScreen();
      friend.emitFlowers(dt);
      
      for (int i = birds.size() - 1; i >= 0; i--) {
        Bird bird = birds.get(i);
        bird.meet(trophee);
        bird.update(dt);
        bird.hover_over();
        bird.constrainToScreen();
        bird.emitSmoke(dt);
      }

      if (isSpaceBarPressed(GameMode.ALL)) {
        this.startup(false);
      }
      break;
    }
  }
  
  void render() {
    landscape.render();
    
    if(trophee != null) {
      trophee.render();
    }
  
    friend.render();  
   
    for (int i = 0; i < pillars.size(); i++) {
      Pillar pillar = pillars.get(i);
      pillar.render();
    }
  
    for (int i = 0; i < birds.size(); i++) {
      Bird bird = birds.get(i);
      bird.render();
    }
  
    if (this.state == GameState.INIT) {
      UI.fadeScreen();
      UI.continueText(ANDROID ? "TOUCH TO START" : "HIT SPACEBAR TO START");
    } else if (this.state == GameState.GAMEOVER) {
      UI.fadeScreen();
      UI.centeredText("GAME OVER");
      UI.continueText(ANDROID ? "TOUCH TO START" : "HIT SPACEBAR TO START");
    } else if (this.state == GameState.GAMEWIN) {
      UI.continueText(ANDROID ? "TOUCH TO START" : "HIT SPACEBAR TO START");
    }
    
    UI.scoreText(String.format("%d", this.score));
  }
  
  void spawnNewPillar() {
    if ((this.frameCounter % this.pillarInterval) == 0 && this.pillarCount < (this.isTraining ? MAX_SCORE_TRAINING : MAX_SCORE)) {
      pillars.add(new Pillar());
      this.pillarCount++;
    }
    this.frameCounter++;
  }
  
  boolean isAudioPlayable() {
    return audioEnabled && birds.size() < 2 && cycles < 6;
  }
}
Game_ Game = new Game_();
