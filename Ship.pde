class Ship {
  PVector location;
  PVector velocity;
  PVector acceleration;
  PVector pullBack;
  int screenLimit;
  float damper;

  Ship() {
    location = new PVector(width/10, height/2);
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    pullBack = new PVector(-1,0);
    screenLimit = 30;
    damper = 0.95;
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
    if (location.y < screenLimit) {
      location.y = screenLimit;
    }
    if (location.y > height-screenLimit) {
      location.y = height-screenLimit;
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
  void display() {
    stroke(0);
    fill(175,70);
    rect(location.x,location.y,50,50);
  }
}
