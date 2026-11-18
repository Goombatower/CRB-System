package hk.edu.polyu.comp.comp2021.crobs.model;
import java.util.HashMap;
import java.util.Objects;

/**
 *  Store all created users rooms and bookings by HashMap;
 */
public class Storage {
    public Storage(){}
    // use user id to identify user object
    private final HashMap<Integer, User> idUser = new HashMap<>();
    // create new user and add to hashmap, usertype is set using setter
    public User addUser(String userName){
        int userId;
        if (Objects.equals(userName, "superadmin")){
            userId = 1;
        }else{
        userId = IdGenerator.nextUserID();
        }
        User user = new User(userId, userName, null);
        idUser.put(userId, user);
        return user;
    }
}
