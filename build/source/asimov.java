import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class asimov extends PApplet {


Table table;
String file = "highscore.csv";
Ship ship;
ArrayList<Star> stars = new ArrayList<Star>();
ArrayList<Asteroid> asteroids;
PFont font;
Minim minim; //audio samples are kept in a buffer. They are suitable for shorter sounds
AudioPlayer startupSound;
AudioPlayer gameplaySound;
AudioPlayer explosion;

// The font must be located in the sketch's
// "data" directory to load successfully

boolean saveScoreToggle = false;
boolean keyLeft = false;
boolean keyRight = false;
boolean keyUp = false;
boolean keyDown = false;
String HighScore = "";

int gameStatus = 0; // The integer stores status of the screen
int gameScore;
int highScore_; // "placeholder" variable to convert the score from string to in

// game constants
final int startScreen = 0;
final int playingGame = 1;
final int gameOver = 2;

public void setup() {
  
  //size(1200,700,P3D);
  
  // Setting up highscore textfile
  table = loadTable("data/"+file, "header");
  if (table == null) {
    makeFile(); // if there is no file yet, create a new one
  }
  // Setting up the stars once, they don't need to be reloaded like the gameSetup
  for (int i = 0; i < width; i++) {
    stars.add(new Star());
  }
  gameSetup();
  font = createFont("robotoMonoMedium.ttf", 32);
}
public void draw() {
  textFont(font);
  switch(gameStatus) {
    case startScreen:
      drawStartScreen();
    break;
    case playingGame: // If we are in the game, draw the game, etc
      drawGame();
    break;
    case gameOver:
      drawGameOverScreen();
    break;
  }
}
public void gameSetup() {
  minim = new Minim(this);
  startupSound = minim.loadFile("data/startupSound.mp3");
  gameplaySound = minim.loadFile("data/gameplaySound.mp3");
  explosion = minim.loadFile("data/explosion.wav");
  gameplaySound.setGain(80);
  gameplaySound.setGain(-50);
  saveScoreToggle = false;
  ship = new Ship(new PVector(width/10, height/2));
  // Initialize asteroids
  asteroids = new ArrayList<Asteroid>(); // This was the missing line of code. Before, the array was created above setup(), now a new array is created everytime the game reloads
  for (int i = 0; i < 6; i++) {
    PVector asteroidLocation = new PVector(random(width+50,width+500),random(height)); // Initialize asteroids outside the screen and let them fly in
    asteroids.add(new Asteroid(asteroidLocation,random(8,17)));
  }
  gameScore = 0;
}
public void drawStartScreen() {
  startupSound.play();
  background(0);
  textAlign(CENTER);
  textSize(40);
  fill(255);
  textSize(72);
  text("ASIMOV\n", width/2, height/2);
  textSize(46);
  text("PRESS ENTER TO START",width/2,height/1.5f);
}
public void drawGameOverScreen() {
  explosion.play();
  background(0);
  textAlign(CENTER);
  textSize(40);
  fill(255);
  text("GAME OVER", width/2, height/2.5f);
  if(!saveScoreToggle) {
    saveData(gameScore);
    saveScoreToggle = true;
  }
  if (gameScore != retrieveData()) {
    text("Your Score: "+gameScore,width/2,height/1.8f);
    text("Highscore: "+retrieveData(),width/2,height/1.6f);
  } else if (gameScore == retrieveData()) {
    text("New HighScore! "+gameScore,width/2,height/1.8f);
  }
  text("PRESS ENTER TO RESTART",width/2,height/1.2f);
}
public void drawGame() {
  background(0);
  gameplaySound.play();
  if (gameplaySound.position() == gameplaySound.length()) {
    gameplaySound.rewind();
    gameplaySound.play();
  }
  // Ship
  ship.run();
  float shipAcceleration = 0.5f;
  if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
    PVector up = new PVector(0,-shipAcceleration);
    ship.applyForce(up);
  }
  if (keyDown) {
    PVector down = new PVector(0,shipAcceleration);
    ship.applyForce(down);
  }
  if (keyRight) {
    PVector forward = new PVector(shipAcceleration+1,0); // +1 because the forward force needs to be stronger than the pullBack force
    ship.applyForce(forward);
    ship.drawTail();
  }
  // Asteroids
  for (Asteroid a: asteroids) {
    PVector baseAcceleration = new PVector(-0.2f,0);
      if (gameScore > 800 && gameScore < 1600) {
      PVector addAcceleration = new PVector(-0.3f,0);
      baseAcceleration.mult(0);
      baseAcceleration.add(addAcceleration);
      PVector attractionForce = ship.attract(a);
      a.applyForce(attractionForce);
    } else if (gameScore > 1600 && gameScore < 2200) {
      PVector addAcceleration = new PVector(-0.5f,0);
      baseAcceleration.mult(0);
      baseAcceleration.add(addAcceleration);
    } else if (gameScore > 2200) {
      PVector addAcceleration = new PVector(-0.4f,0);
      baseAcceleration.mult(0);
      baseAcceleration.add(addAcceleration);
      PVector attractionForce = ship.attract(a);
      a.applyForce(attractionForce);
    }
    a.run();
    a.applyForce(baseAcceleration);
  }
  // Star Parallax
  for (Star s: stars) {
    s.run();
    PVector standardAcc = new PVector(5,0);
    s.parallax(standardAcc);
    if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
      PVector up = new PVector(0,-15);
      s.parallax(up);
    }
    if (keyDown) {
      PVector down = new PVector(0,15);
      s.parallax(down);
    }
    if (keyRight) {
      PVector forward = new PVector(60,0);
      s.parallax(forward);
    }
  }
  checkCollision();
  gameScore++;
}
public void checkCollision() {
  for (int i = 0; i < asteroids.size(); i++) {
    Asteroid asteroidObject  = asteroids.get(i);
    // check asteroid against ship
    if (asteroidObject.checkCollision(ship)) {
      gameStatus = gameOver;
    }
  }
}
public void keyPressed() {
  if (keyCode == UP  ||key == 'W'||key == 'w') {
    keyUp = true;
  }
  if (keyCode == DOWN || key == 'S'|| key == 's') {
    keyDown = true;
  }
  if (keyCode == RIGHT || key == 'D'|| key== 'd') {
    keyRight = true;
    gameScore += 10;
  }
  if (key == ENTER) {
    if (gameStatus != playingGame) {
      gameSetup();
      gameStatus = playingGame;
    }
  }
}
public void keyReleased() { // without this, the ship only moves one time the key is pressed
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
public void saveData(int gameScore) {
  TableRow newRow = table.addRow();
  newRow.setString("Score", str(gameScore));
  saveTable(table, "data/"+file);
}
public int retrieveData() {
  // sort the date in order of best score
  //table.sort("Score");
int[] tempArray = new int[table.getRowCount()];
int i = 0;
  for (TableRow row : table.rows()) {
    HighScore = row.getString("Score");
    tempArray[i] = PApplet.parseInt(HighScore);
      i++;
  }
  tempArray =   sort(tempArray);
  //  println(tempArray[tempArray.length-1]);
//  highScore_ = PApplet.parseInt(HighScore); // Convert from String to int
  return tempArray[tempArray.length-1];
}
public void makeFile() {
  table = new Table();
  table.addColumn("Score");
  // table.addColumn("Name");
  TableRow newRow = table.addRow();
//  newRow.setString("Name", name);
  //newRow.setString("Score", str(random(100, 300)));
//  saveTable(table, "data/"+file);
}
class Asteroid {
  PVector location, velocity, acceleration;
  PImage asteroidP;
  float mass, radius, angle, aAcceleration, aVelocity;

  Asteroid(PVector location_, float m) {
    location = location_;
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    mass = m; // Order has to be right! The passed value is always on the right side of the equation.
    radius = mass*2;
    aVelocity = 0;
    aAcceleration = 0;
    asteroidP = loadImage("asteroid_3.png");
  }
  public void run() {
    update();
    checkEdges();
    display();
  }
  public void applyForce(PVector f) {
    PVector force = PVector.div(f, mass);
    acceleration.add(force);
  }
  public void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); // Resetting acceleration, very important!!!111!11!1!
    // println(velocity);

    aAcceleration = velocity.x / 300.0f; // Rotates according to the x velocity
    aVelocity += aAcceleration;
    angle += aAcceleration;
    aAcceleration *= 0; // Reset
  }
  public void checkEdges() {
    if (location.x + radius < 0) {
      location.x = random(width+50,width+500);
      location.y = random(height);
      velocity.mult(random(0.1f,1.2f)); // randomizes the new velocity of the "new" asteroids
    }
  }
  public void collision() {
    fill(255,0,0);
    pushMatrix();
      translate(location.x,location.y);
      ellipse(0, 0, radius*2.2f, radius*2.2f); // ellipse slightly bigger than asteroid itself
    popMatrix();
  }
  public boolean checkCollision(Ship ship) {
    // Circle Collision Detection
    float distance = dist(location.x, location.y, ship.location.x, ship.location.y);
    if (distance < radius + ship.radius) {
      this.collision(); // calls the local collision function (void collision, line 33-35)
      ship.collision(); // calls the collision on that other object
      return true;
    } else {
      return false;
    }
  }
  public void display() {
    // ellipse(location.x,location.y,2*radius,2*radius);
    imageMode(CENTER);
    pushMatrix();
      translate(location.x,location.y,10); // translating explosion ellipse so that it is on the first "layer"
      rotate(angle);
      image(asteroidP,0,0,2*radius,2*radius);
    popMatrix();
  }
}
class Ship {
  PVector location, velocity, acceleration, pullBack;
  PImage spaceship, flame;
  float radius, damper, mass, g;

  Ship(PVector location_) {
    location = location_;
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    pullBack = new PVector(-1,0);
    radius = 50;
    damper = 0.95f;
    mass = 60;
    g = 0.1f; // Gravitational constant 'g'. Increase value here to make the attraction force stronger
    spaceship = loadImage("spaceship.png");
    flame = loadImage("flame.png");
  }
  public void run() {
    update();
    limit();
    display();
  }
  public void applyForce(PVector u) {
    PVector updateLocation = u.get();
    acceleration.add(updateLocation);
  }
  public void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); // Resets the acceleration, so that it does not build up over time
    velocity.mult(damper);
  }
  public void limit() {
    if (location.x > width/2) {
      location.x = width/2;
    }
    if (location.y < radius) {
      location.y = radius;
    }
    if (location.y > height-radius) {
      location.y = height-radius;
    }
    // Setting the position to the point of origin
    if (location.x < width/10) {
      location.x = width/10;
      velocity.mult(0); // Without this, the effect is basically the same, but the values are not. Here it is set to 0, whereas without this, processing is still calculating stuff setting the macbook on fire
    }
    // Pulling the ship back to point of origin
    if (location.x > width/10) {
      acceleration.add(pullBack);
    }
  }
  public PVector attract(Asteroid a) {
    PVector force = PVector.sub(location,a.location); // substracting the ship location and the location of the asteroids gives us the direction PVector
    float distance = force.mag(); // this gives us the magnitude (distance) of the two objects
    distance = constrain(distance,5.0f,25.0f); // constrain because when the objects are very close, we could actually divide by 0. Also constrain the maximum, because we want realistic looking attraction. Otherwise, the objects could be so far away, that the attraction force is too small and they don't attract each other anymore

    force.normalize(); // normalize the direction vector, so that we can apply strength to it by multiplicating the direction vector with strength
    float strength = (g * mass * a.mass) / (distance * distance); // formula for the strength
    force.mult(strength);

    return force;
  }
  public void collision() {
    fill(255,0,0);
    pushMatrix();
      translate(location.x,location.y,10);
      ellipse(0, 0, radius*2.5f, radius*2.5f);
    popMatrix();
  }
  public void display() {
    noStroke();
    fill(0,100);
    rectMode(CENTER); // so that collision detection works properly, because it assumes that the rectangle is a ellipse for checking collision
    imageMode(CENTER);
    pushMatrix();
      translate(location.x,location.y,5); // translating ship so that it is above the stars
      image(spaceship,0,0,radius*2,radius*2);
    popMatrix();
  }
  public void drawTail() {
    int alternator = PApplet.parseInt(location.x + location.y);
    alternator = alternator%2; // Finds the equal division and gives the remaining sum. Its a value that alternates between 0 and 1. Modulo symbol %, finds the remainder when one number is divided by another
    if (alternator == 0) { // if the alternator is zero, draw the rocket tale
      pushMatrix();
      translate(location.x, location.y);
      translate(-radius*2.05f, -20);
      image(flame,0,0,80,40);
      popMatrix();
    }
  }
}
// Based on William Smith's "Parallax"
class Star {
  PVector origin, location, angle;
  float size;
  //color c; // Just brightness/monochromatic looks better

  Star() {
    // Ship Movement Vector controlling the parallax effect
    origin = new PVector(width / 10, height / 2); // Sets point of origin. Calculating distance and angle from that point will determine parallax effect
    size = random(0.1f,3);
    //c = color(random(200,255),random(200,255),random(200,255));
    location = new PVector(random(width * map(size, 1, 7, 7, 1)), random(height * map(size, 1, 7, 7, 1)));

  }
  public void run() {
    display();
  }
  public void parallax(PVector a) {
    PVector updateAngle = a.get();
    angle = new PVector(5,0);
    angle.add(updateAngle);
    // angle = new PVector(mouseX - width / 10, mouseY - height / 2); // Calculating the direction vector in relevance to origin point. Has to be here. Probably because here it goes in draw and in Star() it is "fixed"
    angle.normalize();
    angle.mult(20);
    // angle.mult(dist(width / 10, height / 2, mouseX, mouseY) / 50);
    origin.add(angle); // angle is basically a vector with x and y coordinates, the bigger the value, the greater the parallax effect
    angle.mult(0);
  }
  public void display() {
    pushStyle();
      stroke(random(150,255));
      strokeWeight(size);
      int x = (int) ((location.x - origin.x) * size / 8) % width; // the bigger stars move faster than the small ones, creating the parallax effect
      int y = (int) ((location.y - origin.y) * size / 8) % height;
      if(x < 0) x += width;
      if(y < 0) y += height;
      point(x,y);
    popStyle();
  }
}
  public void settings() {  fullScreen(P3D);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "asimov" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
