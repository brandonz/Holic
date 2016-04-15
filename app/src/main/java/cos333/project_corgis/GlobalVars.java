package cos333.project_corgis;

/**
 * Singleton static class to keep track of global variables (e.g. user id).
 * Usage: Get an instance and call methods
 *   GlobalVars g = GlobalVars.getInstance();
 *   g.setId(1234);
 *   int id = g.getId();
 * Or all at once:
 *   int id = GlobalVars.getInstance().getId();
 */
public class GlobalVars {
    private static GlobalVars instance;

    // Facebook id
    private String userId;
    // first name, from Facebook
    private String fname;
    // last name, from Facbeook
    private String lname;

    // Restrict the constructor from being instantiated
    private GlobalVars() {}

    public void setId(String id){
        userId = id;
    }
    public String getId(){
        return userId;
    }
    public void setfname(String fname) {
        this.fname = fname;
    }
    public String getfname() {
        return fname;
    }
    public void setlname(String fname) {
        this.lname = lname;
    }
    public String getlname() {
        return lname;
    }

    public static synchronized GlobalVars getInstance(){
        if(instance == null){
            instance = new GlobalVars();
        }
        return instance;
    }
}