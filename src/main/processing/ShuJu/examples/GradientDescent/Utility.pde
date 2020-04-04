String getClassInfo(Object o) {
  String[] m = match(o.getClass().getName(), "([a-zA-Z0-9_]*[a-zA-Z0-9])_?$");
  if(m == null) {
    return o.getClass().getName();
  }
  return m[1];
}

int lerpColor3(int c1, int c2, int c3, float amt) {
  if (amt < 0.5) {
    return lerpColor(c1, c2, map(amt, 0, 0.5, 0, 1));
  } else {
    return lerpColor(c2, c3, map(amt, 0.5, 1, 0, 1));
  }
}

static class Action {
  short keyCode;
  String keyString;
  String help;

  Action(short keyCode, String keyString, String help) {
    this.keyCode = keyCode;
    this.keyString = keyString;
    this.help = help;
  }
}
