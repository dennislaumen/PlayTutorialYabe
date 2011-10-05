import org.junit.*;
import java.util.*;

import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "secret", "Bob").save();

        User bob = User.find("byEmail", "bob@gmail.com").first();

        assertNotNull(bob);
        assertEquals("Bob", bob.fullName);
    }

    @Test
    public void tryConnectAsUser() {
        new User("bob@gmail.com", "secret", "Bob").save();

        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
    }

    @Test
    public void createPost() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        new Post(bob, "My first post", "Hello world").save();

        assertEquals(1, Post.count());

        List<Post> bobsPosts = Post.find("byAuthor", bob).fetch();

        assertEquals(1, bobsPosts.size());
        Post firstPost = bobsPosts.get(0);
        assertNotNull(firstPost);
        assertEquals(bob, firstPost.author);
        assertEquals("My first post", firstPost.title);
        assertEquals("Hello world", firstPost.content);
        assertNotNull(firstPost.postedAt);
    }

    @Test
    public void postComments() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        Post bobsPost = new Post(bob, "My first post", "Hello world").save();

        new Comment(bobsPost, "Jeff", "Nice post").save();
        new Comment(bobsPost, "Tom", "I knew that!").save();

        List<Comment> commentsToBobsPost = Comment.find("byPost", bobsPost).fetch();

        assertEquals(2, commentsToBobsPost.size());

        Comment firstComment = commentsToBobsPost.get(0);
        assertNotNull(firstComment);
        assertEquals("Jeff", firstComment.author);
        assertEquals("Nice post", firstComment.content);
        assertNotNull(firstComment.postedAt);
    }

    @Test
    public void useTheCommentsRelation() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        bobPost.addComment("Jeff", "Nice post");
        bobPost.addComment("Tom", "I knew that !");

        assertEquals(1, User.count());
        assertEquals(1, Post.count());
        assertEquals(2, Comment.count());

        bobPost = Post.find("byAuthor", bob).first();
        assertNotNull(bobPost);

        assertEquals(2, bobPost.comments.size());
        assertEquals("Jeff", bobPost.comments.get(0).author);

        // Delete the post
        bobPost.delete();

        // Check that all comments have been deleted
        assertEquals(1, User.count());
        assertEquals(0, Post.count());
        assertEquals(0, Comment.count());
    }
}
