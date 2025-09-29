package bfst22.vector.Graph;

import java.util.ArrayList;

/**
 * This class is responsible for making the route description
 */

public class RouteDescription {
    ArrayList<Direction> directions;
    short roundaboutExit = 0;

    /**
     * The constructor creates a new obejct from the class.
     * 
     * @param initialHeading
     * @param streetName
     */

    public RouteDescription(float initialHeading, String streetName) {
        this.directions = new ArrayList<>();
        getCompassDirection(initialHeading, streetName);
    }

    /**
     * This method changes the heading of the route, so if the route goes from one
     * edge to another,
     * it finds out which heading is right
     * 
     * @param newHeading    is the new heading calculated
     * @param oldHeading    is the heading that was on the previus edge
     * @param oldStreetName is the name of the street that the route is exiting
     * @param newStreetName is the name of the street that the route is heading to
     * @param distance      distance on the new road/ street.
     */
    public void changeHeading(float newHeading, float oldHeading, String oldStreetName, String newStreetName,
            double distance) {
        float change = oldHeading - newHeading;
        if (change < 0)
            change += 360;
        if ((change < 340 && change > 20)) {
            if (change > 180) {
                directions.add(new Direction(RouteEnum.Turn_Left, newStreetName));
            } else {
                directions.add(new Direction(RouteEnum.Turn_Right, newStreetName));
            }
            directions.add(new Direction(RouteEnum.Continue, newStreetName));
            getLastDirection().addToDistance(distance);
        } else if (oldStreetName != null) {
            if (!oldStreetName.equals(newStreetName)) {
                directions.add(new Direction(RouteEnum.Continue, newStreetName));
            }
        } else {
            getLastDirection().addToDistance(distance);
        }
    }

    /**
     * This method makes sure that the route is discriped correctly in terms of
     * roundabouts
     * 
     * @param newStreetName is the name of the street that the route is heading to
     */

    public void directionRoundAbout(String newStreetName) {
        Direction temp = new Direction(RouteEnum.RoundAbout);
        temp.setRoundAboutExit(roundaboutExit);
        directions.add(temp);
        directions.add(new Direction(RouteEnum.Continue, newStreetName));
        roundaboutExit = 0;
    }

    public void addRoundAboutExit() {
        roundaboutExit++;
    }

    public Direction getLastDirection() {
        return directions.get(directions.size() - 1);
    }

    public void addDistanceToLastDirection(double ekstraDistance) {
        getLastDirection().addToDistance(ekstraDistance);
    }

    public void getCompassDirection(float heading, String streetName) {
        RouteEnum direction;
        if (heading > 30 && heading < 60) {
            direction = RouteEnum.NorthWest;
        } else if (heading > 60 && heading < 120) {
            direction = RouteEnum.North;
        } else if (heading > 120 && heading < 150) {
            direction = RouteEnum.NorthEast;
        } else if (heading > 150 && heading < 210) {
            direction = RouteEnum.East;
        } else if (heading > 210 && heading < 240) {
            direction = RouteEnum.SouthEast;
        } else if (heading > 240 && heading < 300) {
            direction = RouteEnum.South;
        } else if (heading > 300 && heading < 330) {
            direction = RouteEnum.SouthWest;
        } else {
            direction = RouteEnum.West;
        }
        directions.add(new Direction(direction, streetName));
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }

    public ArrayList<String> getDirectionsString() {
        ArrayList<String> output = new ArrayList<String>();
        for (Direction direction : directions) {
            output.add(direction.toString());
        }
        return output;
    }

    public void setFinalDirection(float intialHeading, float directionToDistnation) {
        float change = intialHeading - directionToDistnation;
        if (change < 0) {
            change += 360;
        }
        if (change > 180) {
            directions.add(new Direction(RouteEnum.Distination_Right));
        } else {
            directions.add(new Direction(RouteEnum.Distination_Left));
        }
    }
}