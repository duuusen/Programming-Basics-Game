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
    // println(velocity);

    aAcceleration = velocity.x / 300.0; // Rotates according to the x velocity
    aVelocity += aAcceleration;
    angle += aAcceleration;
    aAcceleration *= 0; // Reset
  }
  void checkEdges() {
    if (location.x + radius < 0) {
      location.x = random(width+50,width+500);
      location.y = random(height);
      velocity.mult(random(0.1,1.2)); // randomizes the new velocity of the "new" asteroids
    }
  }
  void collision() {
    fill(255,0,0);
    pushMatrix();
      translate(location.x,location.y);
      ellipse(0, 0, radius*2.2, radius*2.2); // ellipse slightly bigger than asteroid itself
    popMatrix();
  }
  boolean checkCollision(Ship ship) {
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
  void display() {
    // ellipse(location.x,location.y,2*radius,2*radius);
    imageMode(CENTER);
    pushMatrix();
      translate(location.x,location.y,10); // translating explosion ellipse so that it is on the first "layer"
      rotate(angle);
      image(asteroidP,0,0,2*radius,2*radius);
    popMatrix();
  }
}
