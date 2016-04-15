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
    private int userId;

    // Restrict the constructor from being instantiated
    private GlobalVars() {}

    public void setId(int id){
        userId = id;
    }
    public int getId(){
        return userId;
    }

    public static synchronized GlobalVars getInstance(){
        if(instance == null){
            instance = new GlobalVars();
        }
        return instance;
    }
}