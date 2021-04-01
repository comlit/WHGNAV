package com.lit.whgnav;

//Import der benötigten Bibliotheken
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class NavEngine
{

    private double longitude, latitude;
    private ArrayList<Room> rooms = new ArrayList<>();

    public NavEngine()
    {
        fillrooms();
        fillPath();
    }

    /**
     * Gibt den nächsten Raum zurück
     *
     * TODO: NN  Perfektion reverse ehemalige rechnung von anzeige pixel -> koordinaten; ist in der ROmm klasse aber noch nihct perfekt mit trigonometrie
     * @param pLongitude Länge auf der sich das Handy befindet
     * @param pLatitude  Breite auf der sich das Handy befindet
     * @param pFloor     Etage auf dem sich das Handy befindet
     * @return Objekt des nächsten Raums
     */
    public Room getClosestRoom(double pLongitude, double pLatitude, int pFloor)
    {
        //Variablen werden auf Parameter gesetzt
        longitude = pLongitude;
        latitude = pLatitude;

        if(longitude != 0 && latitude != 0)
        {
            //Liste muss kopiert werden um sie bearbeiten zu können
            List<Room> roomList = new ArrayList<>();

            roomList.add(getRooms().get(0));
            roomList.add(getRooms().get(53));
            roomList.add(getRooms().get(126));
            roomList.add(getRooms().get(143));
            roomList.add(getRooms().get(127));
            roomList.add(getRooms().get(128));
            roomList.add(getRooms().get(129));
            roomList.add(getRooms().get(64));
            roomList.addAll(getRooms().subList(28, 49));
            roomList.addAll(getRooms().subList(80, 98));
            roomList.add(getRooms().get(120));
            roomList.add(getRooms().get(123));
            roomList.addAll(getRooms().subList(106, 108));
            roomList.addAll(getRooms().subList(1, 7));
            roomList.addAll(getRooms().subList(10, 16));
            roomList.addAll(getRooms().subList(19, 25));
            roomList.addAll(getRooms().subList(56, 64));
            roomList.addAll(getRooms().subList(68, 73));
            roomList.addAll(getRooms().subList(74, 79));
            roomList.add(getRooms().get(108));
            roomList.remove(getRooms().get(89));
            roomList.remove(getRooms().get(39));

            //Alle Räume die nicht in der gleichen Etage sind werden gelöcht
            for (Room room: roomList) {
                //Alle Räume die nicht auf der gleichen Etage sind werden aus der Liste gelöscht
                if (room.getFloor() != pFloor) {
                    roomList.remove(room);
                }
            }
            //Initiierung und Derklaration der lokalen Variablen
            int i = 0;
            int indexOfNearest = 0;

            //Index des Raums mit dem gerinsten Abstand zu den Koordinaten wird gefunden
            while (i < roomList.size() && !roomList.isEmpty()) {
                if (getDistanceToCords(roomList.get(indexOfNearest)) > getDistanceToCords(roomList.get(i))) {
                    indexOfNearest = i;
                }
                i++;
            }
            //Wenn die Raumliste nicht Leer ist wird der Raum am  gefunden Index ausgegeben
            if (!roomList.isEmpty()) {
                return roomList.get(indexOfNearest);
            }
        }
        return null;

    }

    /**
     * Methode zum finden des Abstands zwischen den aktuellen Koordinaten und der des augewählten Raums
     *
     * @param pRoom
     * @return
     */
    public double getDistanceToCords(Room pRoom)
    {
        //Satz des Pytagoras zum Berechnen des Abstands
        return Math.pow((Math.pow((longitude - pRoom.getLongitude()), 2) + Math.pow((latitude - pRoom.getLatitude()), 2)), 0.5);
    }

    /**
     * Implemetierung des Dijkstra-Algorithmus
     *
     * @param pStart       Raumobjekt des Starts
     * @param pDestination Raumobjekt des Ziels
     * @return Gibt Namen der Räume entlang des Pfads zurück
     */
    public ArrayList<String> findShortestPath(Room pStart, Room pDestination)
    {
        ArrayList<Room> notUsed = new ArrayList<>();
        Room current;
        ArrayList<String> pathDescription = new ArrayList<>();


        //Distanz zu allen Knoten wird auf "unendlich" gesetzt (ausgenommen wird der Startknoten)
        //Vorgänger der Räume werden auf null gesetzt
        for (Room room: rooms) {
            if (!pStart.equals(room)) {
                room.setDistanceToStart(Double.MAX_VALUE);
            } else {
                room.setDistanceToStart(0);
            }
            room.setPredecessor(null);
            room.setUsed(false);
        }

        //Alle mit dem Startknoten verbundenen Knoten werden zur notUsed-Liste hinzugefügt
        notUsed.addAll(pStart.getConnectedTo());
        for (int i = 0; i < notUsed.size(); i++) {
            notUsed.get(i).setDistanceToStart(pStart.getDistanceTo().get(i));
        }

        //solange es noch Räume zur Überprüfung gibt
        while (!notUsed.isEmpty()) {
            current = notUsed.get(0);
            //Objekt mit dem geringsten Abstand zum Startknoten wird zwischengespeichert
            for (int i = 0; i < notUsed.size(); i++) {
                if (current.getDistanceToStart() > notUsed.get(i).getDistanceToStart()) {
                    current = notUsed.get(i);
                }

            }
            //Liste der mit dem aktuellen Objekt verbundenen Räume wird wird durchlaufen
            for (int i = 0; i < current.getConnectedTo().size(); i++) {
                //Räume die noch nicht benutzt wurden
                if (!current.getConnectedTo().get(i).isUsed()) {
                    //Wenn die Distanz zwischen dem aktuellen Nachfolger vom aktuellen Objekt und dem Start größer ist als
                    // der Abstand vom aktuellen Objekt + dem der Distanz vom aktuellem Objekt zu seinem aktuellen Nachfogler
                    if (current.getConnectedTo().get(i).getDistanceToStart() > current.getDistanceToStart() + current.getDistance().get(i)) {
                        //Distanz des aktuellen Nachfolgers des aktuellen Objekts wird auf die Distanz zwischen dem aktuellen Objekt und dem Start +
                        //die Distanz der aktuellen und dem Nachfolger des aktuellen Objekts gesetzt
                        current.getConnectedTo().get(i).setDistanceToStart(current.getDistanceToStart() + current.getDistance().get(i));
                        //Vorgänger des aktuellen Nachfolgers wird auf das aktuelle Objekt gesetzt
                        current.getConnectedTo().get(i).setPredecessor(current);
                    }
                    //aktueller Nachfolger wird zur Liste noch abzuarbeitender Räume hinzugefügt
                    notUsed.add(current.getConnectedTo().get(i));
                }

            }
            //aktuelles Objekt wird aus Liste der noch abzuarbeitenden Räume enfernt und auf benutzt gesetzt
            notUsed.remove(current);
            current.setUsed(true);
        }

        Room rueckgabe = pDestination;

        while (rueckgabe != null) {
            pathDescription.add(rueckgabe.toString());
            rueckgabe = rueckgabe.getPredecessor();
        }
        pathDescription.add(pStart.toString());
        Collections.reverse(pathDescription);

        return pathDescription;
    }

    /**
     * Methode zum füllen der Raumliste
     */
    private void fillrooms()
    {
        rooms.add(new Room("Bitte Raum auswählen", "", "", 12, 0, 0,new int[]{1,1}));

        rooms.add(new Room("B 1", "", "", 1, 7.618756, 51.947455, new int[]{490,235}));
        rooms.add(new Room("B 2", "", "", 1, 7.618784, 51.947455, new int[]{450,235}));//2
        rooms.add(new Room("B 3", "", "", 1, 7.618539, 51.947451, new int[]{310,235}));
        rooms.add(new Room("B 4", "", "", 1, 7.618451, 51.947447, new int[]{270,235}));//4
        rooms.add(new Room("B 5", "", "", 1, 7.618214, 51.947447, new int[]{130,235}));
        rooms.add(new Room("B 6", "", "", 1, 7.618125, 51.947417, new int[]{90,270}));//6
        rooms.add(new Room("Rotes Treppenhaus EG", "", "", 1, 7.618818, 51.947572, new int[]{470,125}));
        rooms.add(new Room("Gelbes Treppenhaus EG", "", "", 1, 7.618493, 51.947572, new int[]{290,125}));//8
        rooms.add(new Room("Blaues Treppenhaus EG", "", "", 1, 7.618168, 51.947571, new int[]{117,125}));

        rooms.add(new Room("B 101", "", "", 2, 7.618858, 51.947455, new int[]{490,235}));//10
        rooms.add(new Room("B 102", "", "", 2, 7.618784, 51.947455, new int[]{450,235}));
        rooms.add(new Room("B 103", "", "", 2, 7.618539, 51.947451, new int[]{310,235}));//12
        rooms.add(new Room("B 104", "", "", 2, 7.618451, 51.947447, new int[]{270,235}));
        rooms.add(new Room("B 105", "", "", 2, 7.618214, 51.947447, new int[]{130,235}));//14
        rooms.add(new Room("B 106", "", "", 2, 7.618155, 51.947449, new int[]{95,215}));
        rooms.add(new Room("Rotes Treppenhaus 1.OG", "", "", 2, 7.618818, 51.947572, new int[]{470,135}));//16
        rooms.add(new Room("Gelbes Treppenhaus 1.OG", "", "", 2, 7.618493, 51.947572, new int[]{290,135}));
        rooms.add(new Room("Blaues Treppenhaus 1.OG", "", "", 2, 7.618168, 51.947571, new int[]{117,135}));//18

        rooms.add(new Room("B 201", "", "", 3, 7.618858, 51.947455, new int[]{490,235}));
        rooms.add(new Room("B 202", "", "", 3, 7.618784, 51.947455, new int[]{450,235}));//20
        rooms.add(new Room("B 203", "", "", 3, 7.618539, 51.947451, new int[]{310,235}));
        rooms.add(new Room("B 204", "", "", 3, 7.618451, 51.947447, new int[]{270,235}));//22
        rooms.add(new Room("B 205", "", "", 3, 7.618214, 51.947447, new int[]{130,235}));
        rooms.add(new Room("B 206", "", "", 3, 7.618155, 51.947449, new int[]{95,215}));//24
        rooms.add(new Room("Rotes Treppenhaus 2.OG", "", "", 3, 7.618818, 51.947572, new int[]{470,135}));
        rooms.add(new Room("Gelbes Treppenhaus 2.OG", "", "", 3, 7.618493, 51.947572, new int[]{290,135}));//26
        rooms.add(new Room("Blaues Treppenhaus 2.OG", "", "", 3, 7.618168, 51.947571, new int[]{117,135}));

        rooms.add(new Room("A 1", "", "", 1, 1, 1, new int[]{620,235}));//28
        rooms.add(new Room("A 2", "", "", 1, 1, 1, new int[]{685,310}));
        rooms.add(new Room("A 3", "", "", 1, 1, 1, new int[]{685,380}));//30
        rooms.add(new Room("A 4", "", "", 1, 1, 1, new int[]{685,445}));
        rooms.add(new Room("A 5", "", "", 1, 1, 1, new int[]{685,455}));//32
        rooms.add(new Room("A 6", "", "", 1, 1, 1, new int[]{685,485}));
        rooms.add(new Room("A 7", "", "", 1, 1, 1, new int[]{685,510}));//34
        rooms.add(new Room("A 8", "", "", 1, 1, 1, new int[]{685,635}));
        rooms.add(new Room("A 9", "", "", 1, 1, 1, new int[]{685,760}));//36
        rooms.add(new Room("A 10", "", "", 1, 1, 1, new int[]{715,775}));
        rooms.add(new Room("A 11", "", "", 1, 1, 5, new int[]{770,775}));//38
        rooms.add(new Room("A 12", "", "", 1, 1, 7, new int[]{795,775}));
        rooms.add(new Room("A 13", "", "", 1, 1, 9, new int[]{855,755}));//40
        rooms.add(new Room("A 14", "", "", 1, 1, 1, new int[]{705,725}));
        rooms.add(new Room("A 15", "", "", 1, 1, 1, new int[]{705,640}));//42
        rooms.add(new Room("A 16", "", "", 1, 1, 1, new int[]{705,543}));
        rooms.add(new Room("A 17", "", "", 1, 1, 1, new int[]{705,495}));//44
        rooms.add(new Room("A 18", "", "", 1, 1, 1, new int[]{705,455}));
        rooms.add(new Room("A 19", "", "", 1, 1, 1, new int[]{705,380}));//46
        rooms.add(new Room("A 20", "", "", 1, 1, 1, new int[]{713,275}));
        rooms.add(new Room("A 21", "", "", 1, 1, 1, new int[]{713,195}));//48
        rooms.add(new Room("Schwarzes Treppenhaus EG", "", "", 1, 7.619139, 51.947575, new int[]{645,125}));
        rooms.add(new Room("Halle bei Eingang A2", "", "", 1, 3, 5, new int[]{660,225}));//50
        rooms.add(new Room("Organisationsflur ende Richtung A2", "", "", 1, 6, 3, new int[]{695,235}));
        rooms.add(new Room("Organisationsflur ende Richtung Eingangshalle", "", "", 1, 3, 5, new int[]{695,525}));//52
        rooms.add(new Room("Eingangshalle", "", "", 1, 7.619118, 51.947096, new int[]{650,560}));
        rooms.add(new Room("FlurEG bei Eingangshalle in Richtung bio usw.", "", "", 1, 1, 1, new int[]{697,613}));//54
        rooms.add(new Room("Bei A10 und A9 die Ecke", "", "", 1, 1, 3, new int[]{697,755}));

        rooms.add(new Room("C 1", "", "", 1, 1, 1, new int[]{840,135}));//56
        rooms.add(new Room("C 2", "", "", 1, 1, 1, new int[]{910,135}));
        rooms.add(new Room("C 3", "", "", 1, 1, 1, new int[]{977,135}));//58

        //23.01 Beziehungen bis hier eingetragen + Eingang c4

        rooms.add(new Room("C 4", "", "", 1, 1, 1, new int[]{1045,295}));
        rooms.add(new Room("C 5", "", "", 1, 1, 1, new int[]{1110,295}));//60
        rooms.add(new Room("C 6", "", "", 1, 1, 1, new int[]{1170,295}));
        rooms.add(new Room("C 7", "", "", 1, 1, 1, new int[]{1190,265}));//62
        rooms.add(new Room("C 8", "", "", 1, 1, 1, new int[]{1245,265}));
        rooms.add(new Room("Eingang C4", "", "", 1, 7.619855, 51.947595, new int[]{1045,125}));//64
        rooms.add(new Room("C Treppe 1,5", "", "", 1, 1, 1, new int[]{1090,255}));
        rooms.add(new Room("Ecke C6 / c7", "", "", 1, 1, 1, new int[]{1160,255}));//66
        rooms.add(new Room("Ecke C-Treppe / C 4 ", "", "", 1, 1, 1, new int[]{1045,255}));

        rooms.add(new Room("C 104", "", "", 2, 1, 1, new int[]{1040,295}));//68
        rooms.add(new Room("C 105", "", "", 2, 1, 1, new int[]{1110,295}));
        rooms.add(new Room("C 106", "", "", 2, 1, 1, new int[]{1170,295}));//70
        rooms.add(new Room("C 107", "", "", 2, 1, 1, new int[]{1190,265}));
        rooms.add(new Room("C 108", "", "", 2, 1, 1, new int[]{1230,255}));//72
        rooms.add(new Room("Ecke C106 / C107", "", "", 2, 1, 1, new int[]{1160,255}));

        rooms.add(new Room("C 204", "", "", 3, 1, 1, new int[]{1070,295}));//74
        rooms.add(new Room("C 205", "", "", 3, 1, 1, new int[]{1110,295}));
        rooms.add(new Room("C 206", "", "", 3, 1, 1, new int[]{1170,295}));//76
        rooms.add(new Room("C 207", "", "", 3, 1, 1, new int[]{1190,265}));
        rooms.add(new Room("C 208", "", "", 3, 1, 1, new int[]{1230,255}));//78

        rooms.add(new Room("A 100", "", "", 2, 1, 1, new int[]{645,130}));
        rooms.add(new Room("A 101", "", "", 2, 1, 1, new int[]{620,235}));//80
        rooms.add(new Room("A 102", "", "", 2, 1, 1, new int[]{660,245}));
        rooms.add(new Room("A 103", "", "", 2, 1, 1, new int[]{685,310}));//82
        rooms.add(new Room("A 104", "", "", 2, 1, 1, new int[]{685,380}));
        rooms.add(new Room("A 105", "", "", 2, 1, 1, new int[]{685,455}));//84
        rooms.add(new Room("A 106", "", "", 2, 1, 1, new int[]{685,625}));
        rooms.add(new Room("A 107", "", "", 2, 1, 1, new int[]{685,757}));//86
        rooms.add(new Room("A 108", "", "", 2, 1, 1, new int[]{705,775}));
        rooms.add(new Room("A 109", "", "", 2, 1, 1, new int[]{770,775}));//88
        rooms.add(new Room("A 110", "", "", 2, 1, 1, new int[]{795,775}));
        rooms.add(new Room("A 111", "", "", 2, 1, 1, new int[]{855,755}));//90
        rooms.add(new Room("A 112", "", "", 2, 1, 1, new int[]{705,720}));
        rooms.add(new Room("A 113", "", "", 2, 1, 1, new int[]{705,625}));//92
        rooms.add(new Room("A 114", "", "", 2, 1, 1, new int[]{705,555}));
        rooms.add(new Room("A 115", "", "", 2, 1, 1, new int[]{705,505}));//94
        rooms.add(new Room("A 116", "", "", 2, 1, 1, new int[]{705,430}));
        rooms.add(new Room("A 117", "", "", 2, 1, 1, new int[]{705,350}));//96
        rooms.add(new Room("A 118", "", "", 2, 1, 1, new int[]{705,265}));
        rooms.add(new Room("Ecke A101 / A102", "", "", 2, 1, 1, new int[]{650,230}));//98
        rooms.add(new Room("Ecke A102 / A118", "", "", 2, 1, 1, new int[]{690,230}));
        rooms.add(new Room("Flur ende A115", "", "", 2, 1, 1, new int[]{695,525}));//100
        rooms.add(new Room("Halle Eingang 1.OG", "", "", 2, 7.619151, 51.947103, new int[]{700,570}));
        rooms.add(new Room("Wendeltreppe 1.OG", "", "", 2, 7.619151, 51.947103, new int[]{660,570}));
        rooms.add(new Room("Flur A113", "", "", 2, 1, 1, new int[]{700,610}));
        rooms.add(new Room("Ecke A107 / A108", "", "", 2, 1, 1, new int[]{700,755}));//104
        rooms.add(new Room("Südtrakt Treppe 1.OG", "", "", 2, 1, 1, new int[]{780,747}));

        rooms.add(new Room("AK 8", "", "", 0, 1, 1, new int[]{855,755}));//106
        rooms.add(new Room("AK 9", "", "", 0, 1, 1, new int[]{720,740}));

        rooms.add(new Room("CK 2", "", "", 0, 1, 1, new int[]{910,140}));//108
        rooms.add(new Room("Schwarzes Treppenhaus UG", "", "", 0, 7.619139, 51.947575, new int[]{645,125}));
        rooms.add(new Room("VR1C", "", "", 0, 1, 1, new int[]{635,170}));//110
        rooms.add(new Room("VR2C", "", "", 0, 1, 1, new int[]{730,170}));
        rooms.add(new Room("VR3C", "", "", 0, 1, 1, new int[]{745,150}));//112
        rooms.add(new Room("VR4C", "", "", 0, 1, 1, new int[]{780,135}));
        rooms.add(new Room("VR5C", "", "", 0, 1, 1, new int[]{820,130}));//114

        rooms.add(new Room("Südtrakt Treppe EG nach oben", "", "", 1, 1, 1, new int[]{725,747}));

        rooms.add(new Room("Ecke C206 / C207", "", "", 3, 1, 1, new int[]{1160,255}));//116
        rooms.add(new Room("C Treppe 2.OG 0,5", "", "", 3, 1, 1, new int[]{1090,255}));

        rooms.add(new Room("Schwarzes Treppenhaus 1.OG", "", "", 2, 7.619139, 51.947575, new int[]{645,135}));//118
        rooms.add(new Room("Schwarzes Treppenhaus 2.OG", "", "", 3, 7.619139, 51.947575, new int[]{645,135}));
        rooms.add(new Room("A 201", "", "", 3, 1, 1, new int[]{625,235}));//120

        rooms.add(new Room("Südtrakt Treppe UG", "", "", 0, 1, 1, new int[]{725,747}));
        rooms.add(new Room("Südtrakt Treppe EG nach unten", "", "", 1, 1, 1, new int[]{780,747}));//122

        rooms.add(new Room("AK 7", "", "", 0, 1, 1, new int[]{800,775}));
        rooms.add(new Room("VR1AK", "", "", 0, 1, 1, new int[]{725,770}));//124

        rooms.add(new Room("Ecke B6 / B7", "", "", 1, 1, 1, new int[]{110,270}));

        rooms.add(new Room("Eingang A2", "", "", 1, 1, 1, new int[]{615,270}));//126
        rooms.add(new Room("Eingang B1", "", "", 1, 1, 1, new int[]{470,260}));
        rooms.add(new Room("Eingang B2", "", "", 1, 1, 1, new int[]{290,260}));//128
        rooms.add(new Room("Eingang B3", "", "", 1, 1, 1, new int[]{140,270}));
        rooms.add(new Room("Haupteingang", "", "", 1, 1, 1, new int[]{615,560}));//130

        rooms.add(new Room("VR1H", "", "", 2, 1, 1, new int[]{633,585}));
        rooms.add(new Room("VR2H", "", "", 2, 1, 1, new int[]{637,597}));//132
        rooms.add(new Room("VR3H", "", "", 2, 1, 1, new int[]{647,604}));
        rooms.add(new Room("VR4H", "", "", 2, 1, 1, new int[]{659,604}));//134
        rooms.add(new Room("VR5H", "", "", 2, 1, 1, new int[]{669,597}));
        rooms.add(new Room("VR6H", "", "", 2, 1, 1, new int[]{673,585}));//136

        rooms.add(new Room("VR7H", "", "", 1, 1, 1, new int[]{633,585}));
        rooms.add(new Room("VR8H", "", "", 1, 1, 1, new int[]{637,597}));//138
        rooms.add(new Room("VR9H", "", "", 1, 1, 1, new int[]{647,604}));
        rooms.add(new Room("VR10H", "", "", 1, 1, 1, new int[]{659,604}));//140
        rooms.add(new Room("VR11H", "", "", 1, 1, 1, new int[]{669,597}));
        rooms.add(new Room("VR12H", "", "", 1, 1, 1, new int[]{673,585}));//142

        rooms.add(new Room("Eingang A3", "", "", 1, 1, 1, new int[]{645,115}));

        rooms.add(new Room("c treppe EG", "", "", 1, 1, 1, new int[]{1135,255}));//144
        rooms.add(new Room("c treppe 1.OG", "", "", 2, 1, 1, new int[]{1135,255}));
        rooms.add(new Room("c treppe 2.OG", "", "", 3, 1, 1, new int[]{1135,255}));//146

        rooms.add(new Room("VR1CT", "", "", 1, 1, 1, new int[]{1135,246}));
        rooms.add(new Room("VR2CT", "", "", 1, 1, 1, new int[]{1090,246}));//148

        rooms.add(new Room("VR3CT", "", "", 1, 1, 1, new int[]{1110,263}));
        rooms.add(new Room("VR4CT", "", "", 2, 1, 1, new int[]{1110,263}));//150
        rooms.add(new Room("VR5CT", "", "", 1, 1, 1, new int[]{1090,263}));
        rooms.add(new Room("VR6CT", "", "", 2, 1, 1, new int[]{1135,263}));//152

        rooms.add(new Room("VR7CT", "", "", 2, 1, 1, new int[]{1135,246}));
        rooms.add(new Room("VR8CT", "", "", 2, 1, 1, new int[]{1110,246}));//154
        rooms.add(new Room("VR9CT", "", "", 3, 1, 1, new int[]{1110,246}));
        rooms.add(new Room("VR10CT", "", "", 3, 1, 1, new int[]{1090,246}));//156

        rooms.add(new Room("VR11CT", "", "", 3, 1, 1, new int[]{1135,263}));
        rooms.add(new Room("VR12CT", "", "", 3, 1, 1, new int[]{1110,263}));//158
        rooms.add(new Room("VR13CT", "", "", 3, 1, 1, new int[]{1090,263}));

        rooms.add(new Room("Flur ende A103", "", "", 2, 1, 1, new int[]{695,280}));//160

        //B-Trakt EG Blaues
        rooms.add(new Room("VR1BEG", "", "", 1, 1, 1, new int[]{125,172}));
        rooms.add(new Room("VR2BEG", "", "", 1, 1, 1, new int[]{122,140}));//162
        rooms.add(new Room("VR3BEG", "", "", 1, 1, 1, new int[]{112,140}));

        //B-Trakt EG gelbes
        rooms.add(new Room("VR4BEG", "", "", 1, 1, 1, new int[]{305,172}));//164
        rooms.add(new Room("VR5BEG", "", "", 1, 1, 1, new int[]{302,140}));
        rooms.add(new Room("VR6BEG", "", "", 1, 1, 1, new int[]{292,140}));//166

        //B-Trakt EG rotes
        rooms.add(new Room("VR7BEG", "", "", 1, 1, 1, new int[]{483,172}));
        rooms.add(new Room("VR8BEG", "", "", 1, 1, 1, new int[]{480,140}));//168
        rooms.add(new Room("VR9BEG", "", "", 1, 1, 1, new int[]{470,140}));

        //B-Trakt EG schwarzes
        rooms.add(new Room("VR10BEG", "", "", 1, 1, 1, new int[]{659,172}));//170
        rooms.add(new Room("VR11BEG", "", "", 1, 1, 1, new int[]{656,140}));
        rooms.add(new Room("VR12BEG", "", "", 1, 1, 1, new int[]{646,140}));//172

        //B-Trakt 1OG Blaues
        rooms.add(new Room("VR1B1OG", "", "", 2, 1, 1, new int[]{125,172}));
        rooms.add(new Room("VR2B1OG", "", "", 2, 1, 1, new int[]{122,140}));//174
        rooms.add(new Room("VR3B1OG", "", "", 2, 1, 1, new int[]{100,140}));
        rooms.add(new Room("VR4B1OG", "", "", 2, 1, 1, new int[]{98,157}));//176
        rooms.add(new Room("VR5B1OG", "", "", 3, 1, 1, new int[]{125,172}));
        rooms.add(new Room("VR6B1OG", "", "", 3, 1, 1, new int[]{122,140}));//178
        rooms.add(new Room("VR7B1OG", "", "", 3, 1, 1, new int[]{100,140}));
        rooms.add(new Room("VR8B1OG", "", "", 3, 1, 1, new int[]{97,172}));//180

        //B-Trakt 1OG gelbes
        rooms.add(new Room("VR9B1OG", "", "", 2, 1, 1, new int[]{305,172}));
        rooms.add(new Room("VR10B1OG", "", "", 2, 1, 1, new int[]{302,140}));//182
        rooms.add(new Room("VR11B1OG", "", "", 2, 1, 1, new int[]{282,140}));
        rooms.add(new Room("VR12B1OG", "", "", 2, 1, 1, new int[]{280,157}));//184
        rooms.add(new Room("VR13B1OG", "", "", 3, 1, 1, new int[]{305,172}));
        rooms.add(new Room("VR14B1OG", "", "", 3, 1, 1, new int[]{302,140}));//186
        rooms.add(new Room("VR15B1OG", "", "", 3, 1, 1, new int[]{282,140}));
        rooms.add(new Room("VR16B1OG", "", "", 3, 1, 1, new int[]{279,172}));//188

        //B-Trakt 1OG rotes
        rooms.add(new Room("VR17B1OG", "", "", 2, 1, 1, new int[]{483,172}));
        rooms.add(new Room("VR18B1OG", "", "", 2, 1, 1, new int[]{480,140}));//190
        rooms.add(new Room("VR19B1OG", "", "", 2, 1, 1, new int[]{460,140}));
        rooms.add(new Room("VR20B1OG", "", "", 2, 1, 1, new int[]{458,157}));//192
        rooms.add(new Room("VR21B1OG", "", "", 3, 1, 1, new int[]{483,172}));
        rooms.add(new Room("VR22B1OG", "", "", 3, 1, 1, new int[]{480,140}));//194
        rooms.add(new Room("VR23B1OG", "", "", 3, 1, 1, new int[]{460,140}));
        rooms.add(new Room("VR24B1OG", "", "", 3, 1, 1, new int[]{457,172}));//196

        //B-Trakt 1OG schwarzes
        rooms.add(new Room("VR25B1OG", "", "", 2, 1, 1, new int[]{659,172})); //erste mit räumen 1OG
        rooms.add(new Room("VR26B1OG", "", "", 2, 1, 1, new int[]{656,140}));//198
        rooms.add(new Room("VR27B1OG", "", "", 2, 1, 1, new int[]{636,140}));
        rooms.add(new Room("VR28B1OG", "", "", 2, 1, 1, new int[]{634,157}));//200
        rooms.add(new Room("VR29B1OG", "", "", 3, 1, 1, new int[]{659,172}));
        rooms.add(new Room("VR30B1OG", "", "", 3, 1, 1, new int[]{656,140}));//202
        rooms.add(new Room("VR31B1OG", "", "", 3, 1, 1, new int[]{636,140}));
        rooms.add(new Room("VR32B1OG", "", "", 3, 1, 1, new int[]{633,172}));//204 letzte mit räumen 2OG

        //B-Trakt EG Blaues
        rooms.add(new Room("VR13BEG", "", "", 1, 1, 1, new int[]{100,140})); //erste der vierer mit letzte der dreier
        rooms.add(new Room("VR14BEG", "", "", 1, 1, 1, new int[]{97,172}));//206
        rooms.add(new Room("VR15BEG", "", "", 2, 1, 1, new int[]{100,140}));
        rooms.add(new Room("VR16BEG", "", "", 2, 1, 1, new int[]{97,172}));//208 letzte der vierer mit erster der achter + mit beiden räumen

        //B-Trakt EG gelbes
        rooms.add(new Room("VR17BEG", "", "", 1, 1, 1, new int[]{282,140}));
        rooms.add(new Room("VR18BEG", "", "", 1, 1, 1, new int[]{279,172}));//210
        rooms.add(new Room("VR19BEG", "", "", 2, 1, 1, new int[]{282,140}));
        rooms.add(new Room("VR20BEG", "", "", 2, 1, 1, new int[]{279,172}));//212

        //B-Trakt EG rotes
        rooms.add(new Room("VR21BEG", "", "", 1, 1, 1, new int[]{460,140}));
        rooms.add(new Room("VR22BEG", "", "", 1, 1, 1, new int[]{457,172}));//214
        rooms.add(new Room("VR23BEG", "", "", 2, 1, 1, new int[]{460,140}));
        rooms.add(new Room("VR24BEG", "", "", 2, 1, 1, new int[]{457,172}));//216

        //B-Trakt EG schwarzes
        rooms.add(new Room("VR25BEG", "", "", 1, 1, 1, new int[]{636,140}));
        rooms.add(new Room("VR26BEG", "", "", 1, 1, 1, new int[]{633,172}));//218
        rooms.add(new Room("VR27BEG", "", "", 2, 1, 1, new int[]{636,140}));
        rooms.add(new Room("VR28BEG", "", "", 2, 1, 1, new int[]{633,172}));//220

        //TODO: NN Fahgrradkeller und Kiosk verbinden mit allem anderen
        rooms.add(new Room("Fahrradkeller", "", "", 0, 1, 1, new int[]{633,172}));//220
        rooms.add(new Room("Kiosk", "", "", 2, 1, 1, new int[]{633,172}));//220
    }

    /**
     * Wege die die Räume vebinden werden eingespeichert
     */
    private void fillPath()
    {
        //B-Trakt 1OG Blaues
        connectRooms(rooms.get(173), rooms.get(14), 9);
        connectRooms(rooms.get(173), rooms.get(15), 11);

        connectRooms(rooms.get(173), rooms.get(174), 1);
        connectRooms(rooms.get(174), rooms.get(175), 1);
        connectRooms(rooms.get(175), rooms.get(176), 1);
        connectRooms(rooms.get(176), rooms.get(177), 1);
        connectRooms(rooms.get(177), rooms.get(178), 1);
        connectRooms(rooms.get(178), rooms.get(179), 1);
        connectRooms(rooms.get(179), rooms.get(180), 1);

        connectRooms(rooms.get(180), rooms.get(23), 4);
        connectRooms(rooms.get(180), rooms.get(24), 4);


        //B-Trakt 1OG gelbes
        connectRooms(rooms.get(181), rooms.get(12), 9);
        connectRooms(rooms.get(181), rooms.get(13), 9);

        connectRooms(rooms.get(181), rooms.get(182), 1);
        connectRooms(rooms.get(182), rooms.get(183), 1);
        connectRooms(rooms.get(183), rooms.get(184), 1);
        connectRooms(rooms.get(184), rooms.get(185), 1);
        connectRooms(rooms.get(185), rooms.get(186), 1);
        connectRooms(rooms.get(186), rooms.get(187), 1);
        connectRooms(rooms.get(187), rooms.get(188), 1);

        connectRooms(rooms.get(188), rooms.get(21), 4);
        connectRooms(rooms.get(188), rooms.get(22), 4);


        //B-Trakt 1OG rotes
        connectRooms(rooms.get(189), rooms.get(10), 9);
        connectRooms(rooms.get(189), rooms.get(11), 9);

        connectRooms(rooms.get(189), rooms.get(190), 1);
        connectRooms(rooms.get(190), rooms.get(191), 1);
        connectRooms(rooms.get(191), rooms.get(192), 1);
        connectRooms(rooms.get(192), rooms.get(193), 1);
        connectRooms(rooms.get(193), rooms.get(194), 1);
        connectRooms(rooms.get(194), rooms.get(195), 1);
        connectRooms(rooms.get(195), rooms.get(196), 1);

        connectRooms(rooms.get(196), rooms.get(19), 4);
        connectRooms(rooms.get(196), rooms.get(20), 4);


        //B-Trakt 1OG schwarzes
        connectRooms(rooms.get(197), rooms.get(98), 9);

        connectRooms(rooms.get(197), rooms.get(198), 1);
        connectRooms(rooms.get(198), rooms.get(199), 1);
        connectRooms(rooms.get(199), rooms.get(200), 1);
        connectRooms(rooms.get(200), rooms.get(201), 1);
        connectRooms(rooms.get(201), rooms.get(202), 1);
        connectRooms(rooms.get(202), rooms.get(203), 1);
        connectRooms(rooms.get(203), rooms.get(204), 1);

        connectRooms(rooms.get(204), rooms.get(120), 4);


        //B-Trakt EG Blaues
        connectRooms(rooms.get(163), rooms.get(205), 1);
        connectRooms(rooms.get(205), rooms.get(206), 1);
        connectRooms(rooms.get(206), rooms.get(207), 1);
        connectRooms(rooms.get(207), rooms.get(208), 1);
        connectRooms(rooms.get(208), rooms.get(173), 1);

        connectRooms(rooms.get(208), rooms.get(14), 9);
        connectRooms(rooms.get(208), rooms.get(15), 11);

        //B-Trakt EG gelbes
        connectRooms(rooms.get(166), rooms.get(209), 1);
        connectRooms(rooms.get(209), rooms.get(210), 1);
        connectRooms(rooms.get(210), rooms.get(211), 1);
        connectRooms(rooms.get(211), rooms.get(212), 1);
        connectRooms(rooms.get(212), rooms.get(181), 1);

        connectRooms(rooms.get(212), rooms.get(12), 4);
        connectRooms(rooms.get(212), rooms.get(13), 4);

        //B-Trakt EG rotes
        connectRooms(rooms.get(169), rooms.get(213), 1);
        connectRooms(rooms.get(213), rooms.get(214), 1);
        connectRooms(rooms.get(214), rooms.get(215), 1);
        connectRooms(rooms.get(215), rooms.get(216), 1);
        connectRooms(rooms.get(216), rooms.get(189), 1);

        connectRooms(rooms.get(216), rooms.get(10), 4);
        connectRooms(rooms.get(216), rooms.get(11), 4);

        //B-Trakt EG schwarzes
        connectRooms(rooms.get(172), rooms.get(217), 1);
        connectRooms(rooms.get(217), rooms.get(218), 1);
        connectRooms(rooms.get(218), rooms.get(219), 1);
        connectRooms(rooms.get(219), rooms.get(220), 1);
        connectRooms(rooms.get(220), rooms.get(197), 1);

        connectRooms(rooms.get(220), rooms.get(98), 4);




        //b Trakt Treppen EG


        connectRooms(rooms.get(163), rooms.get(9), 1); //Treppenhaus

        connectRooms(rooms.get(161), rooms.get(162), 1);//VRs untereinander
        connectRooms(rooms.get(162), rooms.get(163), 1);


        connectRooms(rooms.get(166), rooms.get(8), 1);//Treppenhaus

        connectRooms(rooms.get(164), rooms.get(165), 1);//VRs untereinander
        connectRooms(rooms.get(165), rooms.get(166), 1);


        connectRooms(rooms.get(169), rooms.get(7), 1);//Treppenhaus

        connectRooms(rooms.get(167), rooms.get(168), 1);//VRs untereinander
        connectRooms(rooms.get(168), rooms.get(169), 1);


        connectRooms(rooms.get(172), rooms.get(49), 11);//Treppenhaus

        connectRooms(rooms.get(170), rooms.get(171), 1);//VRs untereinander
        connectRooms(rooms.get(171), rooms.get(172), 1);



        connectRooms(rooms.get(160), rooms.get(82), 3);
        connectRooms(rooms.get(160), rooms.get(99), 7);
        connectRooms(rooms.get(160), rooms.get(100), 30);

        connectRooms(rooms.get(160), rooms.get(82), 3);
        connectRooms(rooms.get(160), rooms.get(96), 7);
        connectRooms(rooms.get(160), rooms.get(83), 14);
        connectRooms(rooms.get(160), rooms.get(95), 18);
        connectRooms(rooms.get(160), rooms.get(84), 24);
        connectRooms(rooms.get(160), rooms.get(94), 28);
        connectRooms(rooms.get(160), rooms.get(97), 3);

        connectRooms(rooms.get(100), rooms.get(82), 28);
        connectRooms(rooms.get(100), rooms.get(96), 24);
        connectRooms(rooms.get(100), rooms.get(83), 18);
        connectRooms(rooms.get(100), rooms.get(95), 14);
        connectRooms(rooms.get(100), rooms.get(84), 7);
        connectRooms(rooms.get(100), rooms.get(94), 3);

        connectRooms(rooms.get(100), rooms.get(93), 3);

        //VR's für c Treppe
        //connectRooms(rooms.get(144), rooms.get(65), 3);
        connectRooms(rooms.get(144), rooms.get(147), 1);
        connectRooms(rooms.get(147), rooms.get(148), 1);
        connectRooms(rooms.get(148), rooms.get(65), 1);

        connectRooms(rooms.get(144), rooms.get(66), 3);

        //connectRooms(rooms.get(145), rooms.get(65), 3);
        connectRooms(rooms.get(65), rooms.get(151), 1);
        connectRooms(rooms.get(151), rooms.get(149), 1);
        connectRooms(rooms.get(149), rooms.get(150), 1);
        connectRooms(rooms.get(150), rooms.get(152), 1);
        connectRooms(rooms.get(152), rooms.get(145), 1);

        connectRooms(rooms.get(145), rooms.get(73), 3);


        connectRooms(rooms.get(146), rooms.get(116), 3);

        connectRooms(rooms.get(146), rooms.get(157), 1);
        connectRooms(rooms.get(157), rooms.get(158), 1);
        connectRooms(rooms.get(158), rooms.get(159), 1);
        connectRooms(rooms.get(159), rooms.get(117), 1);

        //connectRooms(rooms.get(145), rooms.get(117), 3);
        connectRooms(rooms.get(145), rooms.get(153), 1);
        connectRooms(rooms.get(153), rooms.get(154), 1);
        connectRooms(rooms.get(154), rooms.get(155), 1);
        connectRooms(rooms.get(155), rooms.get(156), 1);
        connectRooms(rooms.get(156), rooms.get(117), 1);


        //VR's für Wendeltreppe
        connectRooms(rooms.get(102), rooms.get(131), 1);

        connectRooms(rooms.get(131), rooms.get(132), 1);
        connectRooms(rooms.get(132), rooms.get(133), 1);
        connectRooms(rooms.get(133), rooms.get(134), 1);
        connectRooms(rooms.get(134), rooms.get(135), 1);
        connectRooms(rooms.get(135), rooms.get(136), 1);
        connectRooms(rooms.get(136), rooms.get(137), 1);
        connectRooms(rooms.get(137), rooms.get(138), 1);
        connectRooms(rooms.get(138), rooms.get(139), 1);
        connectRooms(rooms.get(139), rooms.get(140), 1);
        connectRooms(rooms.get(140), rooms.get(141), 1);
        connectRooms(rooms.get(141), rooms.get(142), 1);

        connectRooms(rooms.get(142), rooms.get(53), 1);



        connectRooms(rooms.get(121), rooms.get(124), 22);
        connectRooms(rooms.get(124), rooms.get(123), 22);
        connectRooms(rooms.get(123), rooms.get(106), 22);

        //B-Trakt 2.OG + A201
        connectRooms(rooms.get(25), rooms.get(19), 7);
        connectRooms(rooms.get(25), rooms.get(20), 7);
        connectRooms(rooms.get(26), rooms.get(21), 7);
        connectRooms(rooms.get(26), rooms.get(22), 7);
        connectRooms(rooms.get(27), rooms.get(23), 7);
        connectRooms(rooms.get(27), rooms.get(24), 7);
        connectRooms(rooms.get(119), rooms.get(120), 7);

        //B-Trakt 1.OG
        connectRooms(rooms.get(16), rooms.get(10), 7);
        connectRooms(rooms.get(16), rooms.get(11), 7);
        connectRooms(rooms.get(17), rooms.get(12), 7);
        connectRooms(rooms.get(17), rooms.get(13), 7);
        connectRooms(rooms.get(18), rooms.get(14), 7);
        connectRooms(rooms.get(18), rooms.get(15), 7);

        // B-Trakt verbindung 1.OG zu 2.OG + schwarzes treppenhaus
        connectRooms(rooms.get(16), rooms.get(25), 7);
        connectRooms(rooms.get(17), rooms.get(26), 7);
        connectRooms(rooms.get(18), rooms.get(27), 7);

        //A-Trakt 1.OG
        connectRooms(rooms.get(80), rooms.get(98), 4);
        connectRooms(rooms.get(81), rooms.get(98), 4);
        connectRooms(rooms.get(99), rooms.get(98), 6);
        connectRooms(rooms.get(99), rooms.get(97), 4);
        connectRooms(rooms.get(97), rooms.get(82), 7);
        connectRooms(rooms.get(118), rooms.get(98), 7);

        connectRooms(rooms.get(82), rooms.get(96), 7);
        connectRooms(rooms.get(96), rooms.get(83), 7);
        connectRooms(rooms.get(83), rooms.get(95), 7);
        connectRooms(rooms.get(95), rooms.get(84), 7);
        connectRooms(rooms.get(84), rooms.get(94), 7);
        connectRooms(rooms.get(94), rooms.get(100), 4);

        connectRooms(rooms.get(102), rooms.get(101), 6);
        connectRooms(rooms.get(101), rooms.get(100), 4);
        connectRooms(rooms.get(101), rooms.get(103), 6);
        connectRooms(rooms.get(101), rooms.get(93), 3);

        connectRooms(rooms.get(103), rooms.get(85), 7);
        connectRooms(rooms.get(103), rooms.get(91), 7);
        connectRooms(rooms.get(103), rooms.get(92), 7);

        connectRooms(rooms.get(104), rooms.get(85), 18);
        connectRooms(rooms.get(104), rooms.get(91), 16);
        connectRooms(rooms.get(104), rooms.get(86), 3);
        connectRooms(rooms.get(104), rooms.get(87), 3);
        connectRooms(rooms.get(104), rooms.get(88), 8);

        connectRooms(rooms.get(105), rooms.get(88), 4);
        connectRooms(rooms.get(105), rooms.get(90), 9);
        connectRooms(rooms.get(88), rooms.get(90), 11);
        connectRooms(rooms.get(105), rooms.get(115), 6);

        //A + B-Trakt verbindung 1.Og zu EG
        connectRooms(rooms.get(7), rooms.get(16), 7);
        connectRooms(rooms.get(8), rooms.get(17), 7);
        connectRooms(rooms.get(9), rooms.get(18), 7);
        connectRooms(rooms.get(115), rooms.get(55), 10);
        connectRooms(rooms.get(49), rooms.get(118), 7);


        //c-Trakt 2.OG
        connectRooms(rooms.get(116), rooms.get(76), 7);
        connectRooms(rooms.get(116), rooms.get(77), 7);
        connectRooms(rooms.get(77), rooms.get(78), 7);
        connectRooms(rooms.get(76), rooms.get(75), 7);
        connectRooms(rooms.get(75), rooms.get(74), 7);

        //c-Trakt 1.OG
        connectRooms(rooms.get(73), rooms.get(71), 7);
        connectRooms(rooms.get(71), rooms.get(72), 7);
        connectRooms(rooms.get(70), rooms.get(73), 7);
        connectRooms(rooms.get(69), rooms.get(70), 7);
        connectRooms(rooms.get(68), rooms.get(69), 7);

        //b trakt eg
        connectRooms(rooms.get(1), rooms.get(2), 3);
        connectRooms(rooms.get(1), rooms.get(167), 10);

        connectRooms(rooms.get(2), rooms.get(167), 10);

        connectRooms(rooms.get(3), rooms.get(4), 3);
        connectRooms(rooms.get(3), rooms.get(164), 10);

        connectRooms(rooms.get(4), rooms.get(164), 10);

        connectRooms(rooms.get(5), rooms.get(6), 5.5);
        connectRooms(rooms.get(5), rooms.get(161), 11);

        connectRooms(rooms.get(6), rooms.get(161), 16);


        connectRooms(rooms.get(9), rooms.get(8), 25);
        connectRooms(rooms.get(8), rooms.get(7), 25);
        connectRooms(rooms.get(49), rooms.get(7), 25);
        connectRooms(rooms.get(64), rooms.get(49), 50);//schwarz eg -> Eingang C 4
        connectRooms(rooms.get(56), rooms.get(49), 25);
        connectRooms(rooms.get(56), rooms.get(57), 9);
        connectRooms(rooms.get(57), rooms.get(58), 9);
        connectRooms(rooms.get(58), rooms.get(64), 9);

        connectRooms(rooms.get(50), rooms.get(28), 3);
        connectRooms(rooms.get(50), rooms.get(47), 3);
        connectRooms(rooms.get(50), rooms.get(48), 3);
        connectRooms(rooms.get(51), rooms.get(29), 3);
        connectRooms(rooms.get(51), rooms.get(30), 3);
        connectRooms(rooms.get(51), rooms.get(46), 3);

        connectRooms(rooms.get(52), rooms.get(32), 3);
        connectRooms(rooms.get(52), rooms.get(33), 3);
        connectRooms(rooms.get(52), rooms.get(44), 3);
        connectRooms(rooms.get(52), rooms.get(45), 3);
        connectRooms(rooms.get(52), rooms.get(51), 33);
        connectRooms(rooms.get(52), rooms.get(54), 11);
        connectRooms(rooms.get(52), rooms.get(43), 3);
        connectRooms(rooms.get(52), rooms.get(53), 6);

        connectRooms(rooms.get(31),rooms.get(30),17 );
        connectRooms(rooms.get(31),rooms.get(32),17);

        connectRooms(rooms.get(55), rooms.get(37), 3);
        connectRooms(rooms.get(55), rooms.get(36), 3);
        connectRooms(rooms.get(55), rooms.get(41), 3);

        connectRooms(rooms.get(54), rooms.get(35), 3);
        connectRooms(rooms.get(54), rooms.get(42), 3);
        connectRooms(rooms.get(54), rooms.get(43), 8.5);
        connectRooms(rooms.get(54), rooms.get(52), 33);
        connectRooms(rooms.get(54), rooms.get(53), 6);

        connectRooms(rooms.get(50), rooms.get(170), 10);

        connectRooms(rooms.get(50), rooms.get(51), 9);

        connectRooms(rooms.get(55), rooms.get(54), 19);
        connectRooms(rooms.get(55), rooms.get(38), 10);

        connectRooms(rooms.get(38), rooms.get(39), 3);
        connectRooms(rooms.get(40), rooms.get(39), 6);
        connectRooms(rooms.get(40), rooms.get(38), 10);
        connectRooms(rooms.get(38), rooms.get(37), 4);
        connectRooms(rooms.get(39), rooms.get(122), 3);
        connectRooms(rooms.get(40), rooms.get(122), 9);
        connectRooms(rooms.get(122), rooms.get(121), 7);
        connectRooms(rooms.get(121), rooms.get(107), 1);

        connectRooms(rooms.get(53), rooms.get(43), 7);

        //connectRooms(rooms.get(107), rooms.get(106), 20);


        //C-trakt EG
        connectRooms(rooms.get(64), rooms.get(67), 18);
        connectRooms(rooms.get(67), rooms.get(59), 6);
        connectRooms(rooms.get(59), rooms.get(60), 8);
        connectRooms(rooms.get(60), rooms.get(61), 7);
        connectRooms(rooms.get(61), rooms.get(66), 6);
        connectRooms(rooms.get(66), rooms.get(62), 5);
        connectRooms(rooms.get(62), rooms.get(63), 6);
        connectRooms(rooms.get(67), rooms.get(65), 10);

        //keller CK2
        connectRooms(rooms.get(49), rooms.get(109), 7);
        connectRooms(rooms.get(109), rooms.get(110), 7);
        connectRooms(rooms.get(110), rooms.get(111), 7);
        connectRooms(rooms.get(111), rooms.get(112), 7);
        connectRooms(rooms.get(112), rooms.get(113), 7);
        connectRooms(rooms.get(113), rooms.get(114), 7);
        connectRooms(rooms.get(114), rooms.get(108), 7);


        connectRooms(rooms.get(143), rooms.get(49), 1);

        //Eingänge unterstufen Schulhof

        connectRooms(rooms.get(130), rooms.get(126), 36);
        connectRooms(rooms.get(130), rooms.get(127), 40);
        connectRooms(rooms.get(130), rooms.get(128), 45);
        connectRooms(rooms.get(130), rooms.get(129), 60);

        connectRooms(rooms.get(126), rooms.get(127), 30);
        connectRooms(rooms.get(127), rooms.get(128), 30);
        connectRooms(rooms.get(128), rooms.get(129), 30);

        connectRooms(rooms.get(126), rooms.get(50), 7);
        connectRooms(rooms.get(127), rooms.get(167), 11);
        connectRooms(rooms.get(128), rooms.get(164), 11);
        connectRooms(rooms.get(130), rooms.get(53), 5);

        connectRooms(rooms.get(127), rooms.get(1), 5);
        connectRooms(rooms.get(127), rooms.get(2), 5);
        connectRooms(rooms.get(128), rooms.get(3), 5);
        connectRooms(rooms.get(128), rooms.get(4), 5);

        connectRooms(rooms.get(129), rooms.get(125), 4);
        connectRooms(rooms.get(125), rooms.get(5), 3);
        connectRooms(rooms.get(125), rooms.get(6), 3);

    }

    /**
     * Methode zu verbinden zwei Raumobjekte
     *
     * @param pFirst    Erster zu verbindener Raum
     * @param pSecond   Zweiter zu verbindener Raum
     * @param pDistance Distanz der Räume
     */
    public void connectRooms(Room pFirst, Room pSecond, double pDistance)
    {
        pFirst.addConnected(pSecond);
        pFirst.addDistance(pDistance);
        pSecond.addConnected(pFirst);
        pSecond.addDistance(pDistance);
    }

    public ArrayList<Room> getRooms()
    {
        return rooms;
    }
}