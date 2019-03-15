package com.android.personalbest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.personalbest.UIdisplay.FriendsUI;
import com.android.personalbest.UIdisplay.GetToKnowYouUI;
import com.android.personalbest.UIdisplay.HomeUI;
import com.android.personalbest.UIdisplay.LoginUI;
import com.android.personalbest.UIdisplay.MessagesUI;
import com.android.personalbest.firestore.FirestoreFactory;
import com.android.personalbest.firestore.IFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.robolectric.util.FragmentTestUtil.startFragment;


@RunWith(RobolectricTestRunner.class)
public class DisplayMessagesFirestoreTest {

    private static final String TEST_SERVICE = "TEST_SERVICE";
    private static final String FIRESTORE_SERVICE_KEY = "FIRESTORE_SERVICE_KEY";
    private MainActivity activity;
    private MessagesUI messagesUI;

    @Before
    public void setUp() throws Exception {
        FirestoreFactory.put(TEST_SERVICE, new FirestoreFactory.BluePrint() {
            @Override
            public IFirestore create(Activity activity, String userEmail) {
                return new TestFirestore(activity, userEmail);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra(FIRESTORE_SERVICE_KEY, TEST_SERVICE);
        intent.putExtra("key", "test");
        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();

        Intent messagesIntent = new Intent(RuntimeEnvironment.application, MessagesUI.class);
        messagesIntent.putExtra("friend_id", "user2email");
        messagesUI = Robolectric.buildActivity(MessagesUI.class, messagesIntent).create().get();
    }


    @Test
    public void testMessagesInCorrectOrder() {
        String expectedOrder = "user1email:\nthis is a test message\n---\nuser2email:\nthis is a test reply\n---\n";
        assertEquals(expectedOrder, ((TextView) messagesUI.findViewById(R.id.chat)).getText().toString());
    }


    @Test
    public void testFriendEmailHeader() {
        Toolbar header = messagesUI.findViewById(R.id.header);
        assertEquals(header.getTitle(), "user2email");
    }


    private class TestFirestore implements IFirestore {

        private static final String TAG = "[TestFirestore]: ";
        private Activity activity;
        private String userEmail;
        private User user;


        public TestFirestore(Activity activity, String userEmail) {
            this.activity = activity;
            this.userEmail = userEmail;
        }


        @Override
        public void setName(String name) {
            Log.d(TAG, "Setting name " + name + " to database");
        }

        @Override
        public void setGoal(int goal) {

        }

        @Override
        public void setHeightFt(int heightFt) {

        }

        @Override
        public void setHeightIn(int heightIn) {

        }

        @Override
        public void initMessageUpdateListener(TextView chatView, String otherUserEmail) {
            StringBuilder sb = new StringBuilder();
            sb.append("user1email");
            sb.append(":\n");
            sb.append("this is a test message");
            sb.append("\n");
            sb.append("---\n");

            sb.append("user2email");
            sb.append(":\n");
            sb.append("this is a test reply");
            sb.append("\n");
            sb.append("---\n");
            chatView.append(sb.toString());
        }

        @Override
        public void addSentMessageToDatabase(EditText editText, String otherUserEmail) {
            Log.d(TAG, "Adding message to database");
        }

        @Override
        public void initMainActivity(MainActivity mainActivity, HomeUI homeUI) {
            this.user=new User();
            List<String> friendsList = new ArrayList<>();
            friendsList.add("user2email");
            user.setName("user1");
            user.setEmail("user1email");
            user.setGoal(2000);
            user.setHeightFt(3);
            user.setHeightIn(6);
            user.setFriends(friendsList);

            MainActivity.setCurrentUser(user);
            mainActivity.loadFragment(homeUI);
        }

        @Override
        public void loginCheckIfUserExists(String otherUserEmail, LoginUI loginUI) {

        }

        @Override
        public void getToKnowYouCheckIfUserExists(String otherUserEmail, GetToKnowYouUI getToKnowYouUI) {

        }

        @Override
        public void addUserToFirestore(User user, GetToKnowYouUI getToKnowYouUI) {

        }

        @Override
        public void setIntentionalSteps(User user, long intentionalSteps) {

        }

        @Override
        public void sendFriendRequest(User user, String friendEmail, FriendsUI friendsUI) {

        }

        @Override
        public void acceptFriendRequest(User user, String friendEmail, FriendsUI friendsUI) {

        }

        @Override
        public void declineFriendRequest(User user, String friendEmail, FriendsUI friendsUI) {

        }

        @Override
        public void addUserToPendingFriends(String user, String emailToAdd, boolean sender) {

        }

        @Override
        public void removeUserFromPendingFriends(String user, String emailToRemove) {

        }

        @Override
        public void addUserToFriends(String user, String emailToAdd) {

        }

        @Override
        public void initMessagesUI(MessagesUI messagesUI, String friendEmail) {

        }

        @Override
        public void removeUserFromFriendsList(String user, String emailToRemove) {

        }

        @Override
        public void removeFriend(User user, String emailToRemove, FriendsUI friendsUI) {

        }
    }
}