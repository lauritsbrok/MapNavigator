package bfst22.vector.Graph;

import bfst22.vector.Util.NumberFormatter;

/**
 * This class is responsible for giving the user a textual descripton of the
 * computed route.
 */

public class Direction {
    RouteEnum instructions;
    String streetName;
    double distance;
    short exit;

    /**
     * The constructor creates a new obejct from the class.
     * 
     * @param instructions is the enum that holds the directions
     * @param streetName   is the used to tell if there is a name on the street, so
     *                     the user can receive it.
     */

    Direction(RouteEnum instructions, String streetName) {
        this.instructions = instructions;
        this.streetName = streetName;
    }

    /**
     * This constuctor gives the option to create an object from the class, without
     * the streetname parameter.
     * This constuctor is ment to be used if there is no streetname.
     * 
     * @param instructions is the enum that holds the directions
     */
    Direction(RouteEnum instructions) {
        this.instructions = instructions;
    }

    /**
     * This method calculates the road ahed. If a road is split into different
     * parts.
     * This method takes the distance from one part of the road, and adds it with
     * the next part, to get a complete road/ disctance
     * 
     * @param ekstraDistance holds the ektra distance that is to be calculated to
     *                       the total distance.
     */
    public void addToDistance(double ekstraDistance) {
        distance += ekstraDistance;
    }

    /**
     * This method sets the exit on a roundabout. If the correct exit is not sat,
     * the directions could lead the wrong way around
     * 
     * @param exit is the parameter that sets the exit
     */
    public void setRoundAboutExit(short exit) {
        this.exit = exit;
    }

    /**
     * This method gives the textual instructions for the calculated route
     * 
     * @param direction collects the textual description.
     *                  @return. Returns the instructions
     */

    public String getInstruction(RouteEnum direction) {
        String instruction = "";
        switch (direction) {
            case North:
                instruction = "Go North";
                break;
            case East:
                instruction = "Go East";
                break;
            case NorthEast:
                instruction = "Go NorthEast";
                break;
            case NorthWest:
                instruction = "Go NorthWest";
                break;
            case South:
                instruction = "Go South";
                break;
            case SouthEast:
                instruction = "Go SouthEast";
                break;
            case SouthWest:
                instruction = "Go SouthWest";
                break;
            case West:
                instruction = "Go West";
                break;
            case Continue:
                instruction = "Continue";
                break;
            case Distination_Left:
                instruction = "The Distination Is On Youre Left";
                break;
            case Distination_Right:
                instruction = "This Distination Is On Youre Right";
                break;
            case RoundAbout:
                instruction = "Take " + exit + ". Exit In The RoundAbout";
                break;
            case Turn_Left:
                instruction = "Turn Right";
                break;
            case Turn_Right:
                instruction = "Turn Left";
                break;
            default:
                break;
        }
        return instruction;
    }

    public String toString() {
        String output = "";
        if (instructions != null) {
            output = getInstruction(instructions);
        }
        if (streetName != null) {
            streetName = streetName.substring(0, 1).toUpperCase() + streetName.substring(1);
            output += " On " + streetName;
        }
        if (distance != 0) {
            output += " (" + NumberFormatter.formatDistance(distance) + ")";
        }
        return output;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

}
