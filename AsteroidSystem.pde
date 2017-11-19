class AsteroidSystem {
  ArrayList<Asteroid> asteroids;
  AsteroidSystem() {
    asteroids = new ArrayList<Asteroid>();
  }
  void run() {
    addAsteroids();
    update();
  }
  void addAsteroids() {
    for (int i = 0; i < 6; i++) {
      PVector asteroidLocation = new PVector(random(width+50,width+150),random(height)); // Initialize asteroids outside the screen and let them fly in
      asteroids.add(new Asteroid(asteroidLocation,random(5,25)));
    }
  }
  void update() {
    for (Asteroid a: asteroids) {
      PVector force = new PVector(-0.8,0);
      a.run();
      a.applyForce(force);
    }
    // Iterator<Asteroid> it = asteroids.iterator(); // This special class, Iterator, tells processing to go through the array one at a time, and if elements are removed, not to look at elements twice or skip them.
    // //[full] Using an Iterator object instead of counting with int i
    // while (it.hasNext()) { // .hasNext function tells us, wheter there is a Particle to run and next() function will grab that Particle object
    //   Asteroid a = it.next();
    //   PVector force = new PVector(-0.8,0);
    //   a.run();
    //   a.applyForce(force);
    //   // if (a.isDead()) {
    //   //    it.remove(); // Here, the iterator object does the deleting, making sure everything runs smoothly
    //   // }
    // }
  }
}
