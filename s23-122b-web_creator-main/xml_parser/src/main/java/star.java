public class star {

    private final String name;

    private final int year;

    private final String id;

    public star(String name, int year, String id) {
        this.name = name;
        this.year = year;
        this.id = id;

    }

    public int getYear() {
        return year;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }



    public String toString() {

        return "Name:" + getName() + ", " +
                "Year:" + getYear() + ", " +
                "ID:" + getId() + ".";
    }
    public String[] tocsv() {
        String[] csvlist = {getName(),""+getYear(),getId()};
        //return getName() + ", " + getYear() + ", " +getId() ;
        return csvlist;
    }
}

