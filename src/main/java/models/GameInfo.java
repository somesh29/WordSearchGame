package models;

import enitities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameInfo {
    private List<User> joinedUsers;

    public List<String> getTurnSequence(String lastPlayedUser) {
        int i = 0;
        for(User user : joinedUsers) {
            if(user.getNick().equals(lastPlayedUser)) break;
            i++;
        }

        List<String> turnSequence = new ArrayList<>();
        if(i+1 <= joinedUsers.size()) {
            turnSequence.addAll(joinedUsers.subList(i+1, joinedUsers.size()).stream().map(User::getNick).collect(Collectors.toList()));
        }
        turnSequence.addAll(joinedUsers.subList(0, i+1).stream().map(User::getNick).collect(Collectors.toList()));
        return turnSequence;
    }

    public Map<String, Integer> getScores() {
        return joinedUsers.stream().collect(Collectors.toMap(User::getNick, User::getScore));
    }
}
