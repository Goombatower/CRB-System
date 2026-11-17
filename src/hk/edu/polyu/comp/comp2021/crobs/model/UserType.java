package hk.edu.polyu.comp.comp2021.crobs.model;

public enum UserType {
    USER("user"),
    ADMIN("admin"),
    SUPER_ADMIN("superAdmin");

    // this tokenType is used in command line
    private final String tokenType;
    
    UserType (String type){
        this.tokenType = type;
    }

    public String getTokenType() {
        return this.tokenType;
    }
    public static userType fromToken (String token) {
        if (token == null) {
            // throw new CrobsException.InvalidValueException("missing user type") ?
        }
        for (UserType type : values()) {
            if (type.tokenType.equals(token)) {
                return type;
            }
        }
        // throw new CrobsException.InvalidValueException("unknown user type: " + token) ?
        return null;
    }
}
