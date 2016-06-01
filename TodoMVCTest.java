
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
        add("1");

    }

    @After
    public void clearData() {
        executeJavaScript("localStorage.clear()");

    }

    /* FUNCTIONAL TESTS */

    @Test
    public void testTasksLifeCycle() {

        edit("1", "1 edited canceled").pressEscape();
        assertTasks("1");
        // Complete all
        toggleAll();
        assertTasks("1");

        filterActive();
        assertNoVisibleTasks();

        add("2");
        edit("2", "2 edited").pressEnter();
        // Complete
        toggle("2 edited");
        assertNoVisibleTasks();

        filterCompleted();
        assertVisibleTasks("1", "2 edited");
        // Reopen
        toggle("1");
        assertVisibleTasks("2 edited");

        clearCompleted();
        assertNoVisibleTasks();

        filterAll();
        assertVisibleTasks("1");

        delete("1");
        assertNoTasks();

    }

    /* FUNCTIONAL TESTS */

    // Actions at ALL filter
    @Test
    public void testCompleteAllFiletr() {
        toggle("1");
        assertVisibleTasks("1");
        itemsLeft("0");

    }

    @Test
    public void testCompleteAllAllFilter() {
        toggleAll();
        assertVisibleTasks("1");
        itemsLeft("0");

    }

    @Test
    public void testReOpenAllFilter() {
        toggle("1");
        toggle("1");
        assertTasks("1");
        itemsLeft("1");

    }

    @Test
    public void testReOpenAllAllFilter() {
        toggle("1");
        toggleAll();
        assertTasks("1");
        itemsLeft("1");

    }

    //Actions at ACTIVE filter

    @Test
    public void testDeleteActiveFilter() {
        filterActive();
        add("2");
        delete("1");
        assertTasks("2");
        itemsLeft("1");
    }

    @Test
    public void testCompleteAllActiveFilter() {
        filterActive();
        toggleAll();
        assertNoVisibleTasks();
        itemsLeft("0");
    }

    @Test
    public void testClearCompletedActiveFilter() {
        filterActive();
        toggle("1");
        clearCompleted();
        assertNoTasks();

    }

    @Test
    public void testReOpenAllActiveFilter() {
        filterActive();
        toggle("1");
        toggleAll();
        assertTasks("1");
    }

    // Actions at COMPLETED filter

    @Test
    public void testAddCompletedFilter() {
        filterCompleted();
        add("2");
        assertNoVisibleTasks();
        itemsLeft("2");
    }

    @Test
    public void testEditCompletedFilter() {
        toggle("1");
        filterCompleted();
        edit("1", "1 edited").pressEnter();
        assertTasks("1 edited");
        itemsLeft("0");

    }

    @Test
    public void testDeleteCompletedFilter() {
        toggle("1");
        filterCompleted();
        delete("1");
        assertNoTasks();

    }

    @Test
    public void testReOpenAllCompletedFilter() {
        toggle("1");
        filterCompleted();
        toggleAll();
        assertNoVisibleTasks();
    }

    /* SWITCH BETWEEN FILTER */

    @Test
    public void testSwitchFilter() {
        filterCompleted();
        assertNoVisibleTasks();
        filterActive();
        assertTasks("1");
        filterAll();
        assertTasks("1");
    }

    /* ADDITIONAL EDIT OPERATIONS*/

    @Test
    public void testUndoEditEsc() {
        filterActive();
        edit("1", "1 edited").pressEscape();
        assertTasks("1");

    }

    @Test
    public void testApplyTaskTab() {
        edit("1", "1 edited").pressTab();
        assertTasks("1 edited");
    }

    @Test
    public void testDeleteEmptyText() {
        edit("1", "").pressEnter();
        assertNoTasks();

    }

    @Test
    public void testApplyTaskClickOut() {
        edit("1", "1 edited");
        $("#header").click();
        assertTasks("1 edited");
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

    private void itemsLeft(String countNumber) {
        $("#todo-count>strong").shouldHave(exactText(countNumber));
    }

}