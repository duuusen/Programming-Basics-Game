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
    damper = 0.95;
    mass = 60;
    g = 0.1; // Gravitational constant 'g'. Increase value here to make the attraction force stronger
    spaceship = loadImage("spaceship.png");
    flame = loadImage("flame.png");
  }
  void run() {
    update();
    limit();
    display();
  }
  void applyForce(PVector u) {
    PVector updateLocation = u.get();
    acceleration.add(updateLocation);
  }
  void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); // Resets the acceleration, so that it does not build up over time
    velocity.mult(damper);
  }
  void limit() {
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
  PVector attract(Asteroid a) {
    PVector force = PVector.sub(location,a.location); // substracting the ship location and the location of the asteroids gives us the direction PVector
    float distance = force.mag(); // this gives us the magnitude (distance) of the two objects
    distance = constrain(distance,5.0,25.0); // constrain because when the objects are very close, we could actually divide by 0. Also constrain the maximum, because we want realistic looking attraction. Otherwise, the objects could be so far away, that the attraction force is too small and they don't attract each other anymore

    force.normalize(); // normalize the direction vector, so that we can apply strength to it by multiplicating the direction vector with strength
    float strength = (g * mass * a.mass) / (distance * distance); // formula for the strength
    force.mult(strength);

    return force;
  }
  void collision() {
    fill(255,0,0);
    pushMatrix();
      translate(location.x,location.y,10);
      ellipse(0, 0, radius*2.5, radius*2.5);
    popMatrix();
  }
  void display() {
    noStroke();
    fill(0,100);
    rectMode(CENTER); // so that collision detection works properly, because it assumes that the rectangle is a ellipse for checking collision
    imageMode(CENTER);
    pushMatrix();
      translate(location.x,location.y,5); // translating ship so that it is above the stars
      image(spaceship,0,0,radius*2,radius*2);
    popMatrix();
  }
  void drawTail() {
    int alternator = int(location.x + location.y);
    alternator = alternator%2; // Finds the equal division and gives the remaining sum. Its a value that alternates between 0 and 1. Modulo symbol %, finds the remainder when one number is divided by another
    if (alternator == 0) { // if the alternator is zero, draw the rocket tale
      pushMatrix();
      translate(location.x, location.y);
      translate(-radius*2.05, -20);
      image(flame,0,0,80,40);
      popMatrix();
    }
  }
}
