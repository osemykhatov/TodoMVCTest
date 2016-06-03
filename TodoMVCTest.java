
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;


public class TodoMVCTest {

    private ElementsCollection tasks = $$("#todo-list>li");

    @Before
    public void openPage() {
        open("https://todomvc4tasj.herokuapp.com/");

    }

    @After
    public void clearData() {
        executeJavaScript("localStorage.clear()");

    }


    @Test
    public void testTasksLifeCycle() {

        add("1");
        assertTasks("1");
        // Complete all
        toggleAll();
        assertTasks("1");

        filterActive();
        assertNoVisibleTasks();

        add("2");
        // Complete
        toggle("2");
        assertNoVisibleTasks();

        filterCompleted();
        assertVisibleTasks("1", "2");
        // Reopen
        toggle("1");
        assertVisibleTasks("2");

        clearCompleted();
        assertNoVisibleTasks();

        filterAll();
        assertVisibleTasks("1");

        delete("1");
        assertNoTasks();

    }


    @Test
    public void testUndoEditAtAllFilter() {
        // Given - tasks add
        add("1", "2");

        edit("2", "2 edited").pressEscape();
        assertVisibleTasks("1", "2");
        assertItemsLeft("2");

    }

    @Test
    public void testEditAtActiveFilter() {
        // Given - tasks add -> go to filter
        add("1", "2");
        filterActive();

        edit("1", "1 edited").pressEnter();
        assertVisibleTasks("1 edited", "2");
        assertItemsLeft("2");
    }

    private void add(String... taskTexts) {
        for (String text : taskTexts) {
            $("#new-todo").setValue(text).pressEnter();
        }
    }

    private SelenideElement edit(String oldTaskText, String newTasText) {
        tasks.find(exactText(oldTaskText)).doubleClick();
        return tasks.find(cssClass("editing")).$(".edit").setValue(newTasText);
    }

    private void delete(String taskText) {
        tasks.find(exactText(taskText)).hover().find(".destroy").click();
    }

    private void toggle(String taskText) {
        tasks.find(exactText(taskText)).find(".toggle").click();

    }

    private void toggleAll() {
        $("#toggle-all").click();
    }

    private void clearCompleted() {
        $("#clear-completed").click();
    }

    private void assertTasks(String... taskTexts) {
        tasks.shouldHave(exactTexts(taskTexts));
    }

    private void assertVisibleTasks(String... taskTexts) {
        tasks.filter(visible).shouldHave(exactTexts(taskTexts));
    }

    private void assertNoVisibleTasks() {
        tasks.filter(visible).shouldBe(empty);
    }

    private void assertNoTasks() {
        tasks.shouldBe(empty);
    }

    private void filterActive() {
        $(By.linkText("Active")).click();
    }

    private void filterCompleted() {
        $(By.linkText("Completed")).click();
    }

    private void filterAll() {
        $(By.linkText("All")).click();
    }

    private void assertItemsLeft(String countNumber) {
        $("#todo-count>strong").shouldHave(exactText(countNumber));
    }

}