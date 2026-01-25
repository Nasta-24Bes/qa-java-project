package praktikum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BurgerEdgeCasesTest {

    private AutoCloseable closeable;
    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredient;

    private final float bunPrice;
    private final String testCaseName;

    public BurgerEdgeCasesTest(String testCaseName, float bunPrice) {
        this.testCaseName = testCaseName;
        this.bunPrice = bunPrice;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"Бесплатная булочка", 0.0f},
                {"Булочка с дробной ценой", 1.5f},
                {"Булочка с высокой ценой", 1000.0f}
        });
    }

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        burger = new Burger();

        when(mockBun.getName()).thenReturn("test bun");
        when(mockBun.getPrice()).thenReturn(bunPrice);

        when(mockIngredient.getName()).thenReturn("test ingredient");
        when(mockIngredient.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient.getPrice()).thenReturn(10.0f);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testGetPriceWithZeroPriceBun() {
        burger.setBuns(mockBun);

        float expectedPrice = bunPrice * 2;
        float actualPrice = burger.getPrice();

        assertEquals("Цена с нулевой стоимостью булочки", expectedPrice, actualPrice, 0.001f);
    }

    @Test
    public void testMultipleAddAndRemoveOperations() {
        burger.setBuns(mockBun);

        int numberOfIngredientsToAdd = 3;
        for (int i = 0; i < numberOfIngredientsToAdd; i++) {
            burger.addIngredient(mockIngredient);
        }

        assertEquals("Должно быть " + numberOfIngredientsToAdd + " ингредиента",
                numberOfIngredientsToAdd, burger.ingredients.size());

        int indexToRemove = 1;
        burger.removeIngredient(indexToRemove);

        int expectedIngredientsAfterRemoval = numberOfIngredientsToAdd - 1;
        assertEquals("После удаления должно остаться " + expectedIngredientsAfterRemoval + " ингредиента",
                expectedIngredientsAfterRemoval, burger.ingredients.size());

        float expectedPrice = (bunPrice * 2) + (10.0f * expectedIngredientsAfterRemoval);
        float actualPrice = burger.getPrice();

        assertEquals("Цена после удаления ингредиента", expectedPrice, actualPrice, 0.001f);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientInvalidIndex() {
        burger.addIngredient(mockIngredient);
        int invalidIndex = 5;
        burger.removeIngredient(invalidIndex); // Неверный индекс
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientInvalidIndex() {
        burger.addIngredient(mockIngredient);
        int invalidNewIndex = 5;
        burger.moveIngredient(0, invalidNewIndex); // Неверный индекс
    }

    @Test
    public void testEmptyBurgerReceipt() {
        burger.setBuns(mockBun);

        String receipt = burger.getReceipt();

        assertNotNull("Чек пустого бургера не должен быть null", receipt);

        // Проверка формата чека целиком
        String[] lines = receipt.split("\n");

        assertEquals("Чек пустого бургера должен иметь 4 строки", 4, lines.length);
        assertEquals("Первая строка должна быть в правильном формате",
                "(==== test bun ====)", lines[0].trim());
        assertEquals("Вторая строка должна быть в правильном формате",
                "(==== test bun ====)", lines[1].trim());
        assertTrue("Третья строка должна быть пустой", lines[2].trim().isEmpty());
        assertEquals("Четвертая строка должна содержать цену",
                String.format("Price: %f", bunPrice * 2), lines[3].trim());
    }
}
