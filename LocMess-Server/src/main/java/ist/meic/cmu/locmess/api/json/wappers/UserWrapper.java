package ist.meic.cmu.locmess.api.json.wappers;

import ist.meic.cmu.locmess.domain.users.User;

/**
 * Created by lads on 21-04-2017.
 */
public class UserWrapper {
        String username;
        String name;
        String password;

        public UserWrapper() {
        }

        public UserWrapper(String username, String name, String password) {
            this.username = username;
            this.name = name;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public User createUser() {
            return new User(username, name, password);
        }
}
