class Asteroid {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float mass;
  float radius;
  boolean offScreen = false;

  Asteroid(PVector asteroidLocation, float m) {
    location = asteroidLocation;
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    mass = m; // Order has to be right! The passed value is always on the right side of the equation.
    radius = mass*2;
  }
  void run() {
    update();
    display();
  }
  void applyForce(PVector f) {
    PVector force = PVector.div(f, mass);
    acceleration.add(force);
  }
  void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); // Resetting acceleration, very important!!!111!11!1!
  }
  boolean offScreen() {
    if (location.x+radius < 0) {
      return true;
    } else {
      return false;
    }
  }
  void display() {
    fill(255);
    ellipse(location.x,location.y,2*radius,2*radius);
  }
}
