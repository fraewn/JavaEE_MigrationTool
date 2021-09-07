package core.services;

public interface UserMgmtService {

    public void checkUnique();

    public void getUserGroupByName();

    public void getUserByName();

    public void createUser();

    public void deleteUser();

    public void editUser();

    public void getUserById();

    public void getUserGroupOfUser();

    public void getAllUsers();
}
