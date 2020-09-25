package com.benberi.cadesim.server.model.player.move;

import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

public enum MoveType {

    FORWARD(2),
    LEFT(1),
    RIGHT(3),
    NONE(0);

    private int id;

    MoveType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MoveType forId(int id) {
        for(MoveType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return null;
    }

    public VesselFace getNextFace(VesselFace face) {
        switch (this) {
            case NONE:
            case FORWARD:
                return face;
            case LEFT:
                int newFace = face.getDirectionId() - 4;
                if (newFace < 0) {
                    newFace = 14;
                }
                return VesselFace.forId(newFace);
            case RIGHT:
                newFace = face.getDirectionId() + 4;
                if (newFace > 14) {
                    newFace = 2;
                }

                return VesselFace.forId(newFace);
        }

        return VesselFace.NORTH;
    }


    public Position getNextPositionWithPhase(Position position, VesselFace face, int phase) {
       switch (this) {
           case FORWARD:
               if (phase == 0) {
                   switch (face) {
                       case NORTH:
                           return position.copy().addY(1);
                       case SOUTH:
                           return position.copy().addY(-1);
                       case WEST:
                           return position.copy().addX(-1);
                       case EAST:
                           return position.copy().addX(1);
                   }
                   break;
               }
               else {
                   return position;
               }
           case LEFT:
               switch (face) {
                   case NORTH:
                       if (phase == 0) {
                           return position.copy().addY(1);
                       }
                       else if (phase == 1) {
                           return position.copy().addX(-1);
                       }
                       break;
                   case SOUTH:
                       if (phase == 0) {
                           return position.copy().addY(-1);
                       }
                       else if (phase == 1) {
                           return position.copy().addX(+1);
                       }
                       break;
                   case WEST:
                       if (phase == 0) {
                           return position.copy().addX(-1);
                       }
                       else if (phase == 1) {
                           return position.copy().addY(-1);
                       }
                       break;
                   case EAST:
                       if (phase == 0) {
                           return position.copy().addX(1);
                       }
                       else if (phase == 1) {
                           return position.copy().addY(1);
                       }
                       break;
               }
               break;
           case RIGHT:
               switch (face) {
                   case NORTH:
                       if (phase == 0) {
                           return position.copy().addY(1);
                       }
                       else if (phase == 1) {
                           return position.copy().addX(1);
                       }
                       break;
                   case SOUTH:
                       if (phase == 0) {
                           return position.copy().addY(-1);
                       }
                       else if (phase == 1) {
                           return position.copy().addX(-1);
                       }
                       break;
                   case WEST:
                       if (phase == 0) {
                           return position.copy().addX(-1);
                       }
                       else if (phase == 1) {
                           return position.copy().addY(1);
                       }
                       break;
                   case EAST:
                       if (phase == 0) {
                           return position.copy().addX(1);
                       }
                       else if (phase == 1) {
                           return position.copy().addY(-1);
                       }
                       break;
               }
               break;
           case NONE:
               // pass
           default:
               break;
       }

       return position;
    }

	public Position getFinalPosition(Position position, VesselFace face) {
	       switch (this) {
           case FORWARD:
               switch (face) {
                   case NORTH:
                       return position.copy().addY(1);
                   case SOUTH:
                       return position.copy().addY(-1);
                   case WEST:
                       return position.copy().addX(-1);
                   case EAST:
                       return position.copy().addX(1);
               }
               break;
           case LEFT:
               switch (face) {
                   case NORTH:
                       return position.copy().addY(1).addX(-1);
                   case SOUTH:
                       return position.copy().addY(-1).addX(+1);
                   case WEST:
                       return position.copy().addX(-1).addY(-1);
                   case EAST:
                       return position.copy().addX(1).addY(1);
               }
               break;
           case RIGHT:
               switch (face) {
                   case NORTH:
                       return position.copy().addY(1).addX(1);
                   case SOUTH:
                       return position.copy().addY(-1).addX(-1);
                   case WEST:
                       return position.copy().addX(-1).addY(1);
                   case EAST:
                       return position.copy().addX(1).addY(-1);
               }
               break;
           case NONE:
               // pass
           default:
               break;
       }

       return position;
	}
}
