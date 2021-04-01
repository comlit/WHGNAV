package com.lit.whgnav;

import java.util.ArrayList;
import java.util.List;

public class Room
{
    private String raumId, beschreibung, lehrer;
    private double longitude, latitude;
    private int floor;
    private List<Room> connectedTo;
    private List<Double> distanceTo;
    private boolean isUsed;
    private double distanceToStart;
    private Room predecessor;
    private int[] cords;
    private int relativeToNextRoom;


    public Room(String pRaumId, String pBeschreibung, String pLehrer, int pFloor, double pLongitude, double pLatitude, int[] pCords)
    {
        raumId = pRaumId;
        beschreibung = pBeschreibung;
        lehrer = pLehrer;
        floor = pFloor;
        longitude = pLongitude;
        latitude = pLatitude;
        isUsed = false;
        connectedTo = new ArrayList<>();
        distanceTo = new ArrayList<>();
        distanceToStart = 0;
        predecessor = null;
        cords = pCords;
        relativeToNextRoom = 0;
    }

    public Room getPredecessor()
    {
        return predecessor;
    }

    public void setPredecessor(Room predecessor)
    {
        this.predecessor = predecessor;
    }

    public void setDistanceToStart(double distance)
    {
        this.distanceToStart = distance;
    }

    public double getDistanceToStart()
    {
        return distanceToStart;
    }

    public List<Double> getDistanceTo()
    {
        return distanceTo;
    }

    public void addConnected(Room pRoom)
    {
        connectedTo.add(pRoom);
    }

    public void addDistance(double pDistance)
    {
        distanceTo.add(pDistance);
    }

    public List<Room> getConnectedTo()
    {
        return connectedTo;
    }

    public List<Double> getDistance()
    {
        return distanceTo;
    }

    public boolean isUsed()
    {
        return isUsed;
    }

    public void setUsed(boolean used)
    {
        isUsed = used;
    }

    public String getRaumId()
    {
        return raumId;
    }

    public String getBeschreibung()
    {
        return beschreibung;
    }

    public String getLehrer()
    {
        return lehrer;
    }

    public double getLongitude()
    {
        if(longitude!=1)
        {
            return longitude;
        }
        else
        {
            return cords[0] * 0.000001836572521916 + 7.617941;
        }
    }

    public double getLatitude()
    {
        if(latitude!=1)
        {
            return latitude;
        }
        else
        {
            return cords[1] * 0.0000018365725219163 + 51.947720;
        }
    }

    public int getFloor()
    {
        return floor;
    }

    public boolean isConnectedTo(Room pRoom)
    {
        return connectedTo.contains(pRoom);
    }

    public int getRelativeToNextRoom()
    {
        return relativeToNextRoom;
    }

    public void setRelativeToNextRoom(int relativeToNextRoom)
    {
        this.relativeToNextRoom = relativeToNextRoom;
    }

    @Override
    public String toString()
    {
        String answer = raumId;
        if(!beschreibung.equals(""))
        {
            answer+=" "+beschreibung;
        }
    return answer;
    }

    public int[] getCords()
    {
        return cords;
    }
}
