Ship ship;
ArrayList<Star> stars = new ArrayList<Star>();
ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();


boolean keyLeft = false;
boolean keyRight = false;
boolean keyUp = false;
boolean keyDown = false;

void setup() {
  size(1200,700);
  smooth();
  gameSetup();
}
void draw() {
  drawGame();
}
void gameSetup() {
  ship = new Ship();
  // Initialize stars and asteroids
  for (int i = 0; i < width; i++) {
    stars.add(new Star());
  }
  for (int i = 0; i < 6; i++) {
    PVector asteroidLocation = new PVector(random(width+50,width+150),random(height)); // Initialize asteroids outside the screen and let them fly in
    asteroids.add(new Asteroid(asteroidLocation,random(5,25)));
  }
}
void drawGame() {
  background(0);
  // Ship
  ship.run();
  int shipAcceleration = 2;
  if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
    PVector up = new PVector(0,-shipAcceleration);
    ship.applyForce(up);
  }
  if (keyDown) {
    PVector down = new PVector(0,shipAcceleration);
    ship.applyForce(down);
  }
  if (keyRight) {
    PVector forward = new PVector(shipAcceleration,0);
    ship.applyForce(forward);
  }
  // Asteroids
  for (Asteroid a: asteroids) {
    PVector force = new PVector(-0.8,0);
    a.run();
    a.applyForce(force);
  }
  // Star Parallax
  for (Star s: stars) {
    s.run();
    PVector standardAcc = new PVector(5,0);
    s.parallax(standardAcc);
    if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
      PVector up = new PVector(0,-10);
      s.parallax(up);
    }
    if (keyDown) {
      PVector down = new PVector(0,10);
      s.parallax(down);
    }
    if (keyRight) {
      PVector forward = new PVector(15,0);
      s.parallax(forward);
    }
  }
}
void keyPressed() {

  if (keyCode == UP  ||key == 'W'||key == 'w') {
    keyUp = true;
  }
  if (keyCode == DOWN || key == 'S'|| key == 's') {
    keyDown = true;
  }
  if (keyCode == RIGHT || key == 'D'|| key== 'd') {
    keyRight = true;
  }
  // if (key == ' ' && gameStatus == playingGame && shootLimit == false) { // space key for shooting, just during gameplay
  //   myShip.shoot();
  //   shootLimit = true; // we are allowed to shoot
  // }
  // if (key == ENTER) {
  //   if (gameStatus != playingGame) {
  //     setupGame();
  //     gameStatus = playingGame;
  //   }
  // }
  // if (key == ' ' && gameStatus == playingGame) {
  //   shootLimit = false;
  // }
}
void keyReleased() { // without this, the ship only moves one time the key is pressed
  if (keyCode == UP || key == 'W'|| key == 'w') {
    keyUp = false;
  }
  if (keyCode == DOWN || key == 'S'|| key == 's') {
    keyDown = false;
  }
  if (keyCode == RIGHT || key == 'D'|| key == 'd') {
    keyRight = false;
  }
}
