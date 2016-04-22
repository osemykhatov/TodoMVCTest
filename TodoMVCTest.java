
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;


public class TodoMVCTest {

    private ElementsCollection tasks = $$("#todo-list>li");


    @Test
    public void testTasksLifeCycle() {

        open("https://todomvc4tasj.herokuapp.com/");

        add("1");
        edit("1", "1 edited canceled").pressEscape();
        assertTasks("1");
        toggleAll();
        assertTasks("1");

        filterActive();
        assertInvisibleTasks();

        add("2");
        edit("2", "2 edited").pressEnter();
        toggle("2 edited");
        assertInvisibleTasks();

        filterCompleted();

        assertTasks("1", "2 edited");
        toggle("1");
        assertTasks("2 edited");
        clearCompleted();
        assertInvisibleTasks();

        filterAll();

        assertTasks("1");
        delete("1");
        assertNoTasks();

    }

    private void add(String... taskTexts) {
        for (String text : taskTexts) {
            $("#new-todo").setValue(text).pressEnter();
        }
    }

    private SelenideElement edit(String oldTaskText, String newTasjText) {
        tasks.find(exactText(oldTaskText)).doubleClick();
        return tasks.find(cssClass("editing")).$(".edit").setValue(newTasjText);
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
        tasks.filter(visible).shouldHave(exactTexts(taskTexts));
    }

    private void assertInvisibleTasks() {
        tasks.filter(visible).shouldBe(empty);
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

    private void assertNoTasks() {
        tasks.shouldBe(empty);
    }


}