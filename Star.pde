// Based on William Smith's "Parallax"
class Star {
  PVector origin, location, angle;
  float size;
  //color c; // Just brightness/monochromatic looks better

  Star() {
    // Ship Movement Vector controlling the parallax effect
    origin = new PVector(width / 10, height / 2); // Sets point of origin. Calculating distance and angle from that point will determine parallax effect
    size = random(0.1,3);
    //c = color(random(200,255),random(200,255),random(200,255));
    location = new PVector(random(width * map(size, 1, 7, 7, 1)), random(height * map(size, 1, 7, 7, 1)));

  }
  void run() {
    display();
  }
  void parallax(PVector a) {
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
  void display() {
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
