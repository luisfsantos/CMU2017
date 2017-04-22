package ist.meic.cmu.locmess.api.json.wrappers;

import ist.meic.cmu.locmess.domain.users.User;

/**
 * Created by lads on 21-04-2017.
 */
public class UserWrapper {
        String username;
        @Deprecated
        String name;

        String password;

        public UserWrapper() {
        }
        @Deprecated
        public UserWrapper(String username, String name, String password) {
            this.username = username;
            this.name = name;
            this.password = password;
        }

        public UserWrapper(String username, String password) {
            this.username = username;
            this.name = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Deprecated
        public String getName() {
            return name;
        }
        @Deprecated
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
