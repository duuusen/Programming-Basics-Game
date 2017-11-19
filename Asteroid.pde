class Asteroid {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float mass;
  float radius;

  Asteroid(PVector asteroidLocation, float m) {
    location = asteroidLocation;
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    mass = m; // Order has to be right! The passed value is always on the right side of the equation.
    radius = mass*2;
  }
  void run() {
    update();
    checkEdges();
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
    println(velocity);
  }
  void checkEdges() {
    if (location.x+radius < 0) {
      location.x = random(width+50,width+500);
      location.y = random(height);
      velocity.mult(random(0.1,1.2)); // randomizes the new velocity of the "new" asteroids
    }
  }
  void collision() {
    fill(255,0,0);
    ellipse(location.x, location.y, 60, 60); // ellipse slightly bigger than asteroid itself
  }
  boolean checkCollision(Ship ship) {
    // Circle Collision Detection
    float distance = dist(location.x, location.y, ship.x(), ship.y());
    if (distance < radius + ship.radius) {
      this.collision(); // calls the local collision function (void collision, line 33-35)
      ship.collision(); // calls the collision on that other object
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
