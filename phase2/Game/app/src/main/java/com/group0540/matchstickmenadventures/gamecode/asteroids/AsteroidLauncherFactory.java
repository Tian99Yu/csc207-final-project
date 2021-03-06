package com.group0540.matchstickmenadventures.gamecode.asteroids;

class AsteroidLauncherFactory {
  private AsteroidLauncherFactory() {}

  static WeaponSystem<Asteroid> getAsteroidLauncher(AsteroidLauncherType asteroidLauncherType) {
    switch (asteroidLauncherType) {
      case STANDARD_ASTEROID_LAUNCHER:
        return new AsteroidLauncher(250, 0.1, 150, 20, 1, 0);
      default:
        return null;
    }
  }
}
