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
public class BurgerTest {

    private AutoCloseable closeable;
    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredientSauce;

    @Mock
    private Ingredient mockIngredientFilling;

    @Mock
    private Ingredient mockIngredientSauceSecond;

    private final String bunName;
    private final float bunPrice;

    public BurgerTest(String bunName, float bunPrice) {
        this.bunName = bunName;
        this.bunPrice = bunPrice;
    }

    @Parameterized.Parameters(name = "Булочка: {0}, Цена: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"black bun", 100.0f},
                {"white bun", 200.0f},
                {"red bun", 300.0f}
        });
    }

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        burger = new Burger();

        when(mockBun.getName()).thenReturn(bunName);
        when(mockBun.getPrice()).thenReturn(bunPrice);

        when(mockIngredientSauce.getName()).thenReturn("hot sauce");
        when(mockIngredientSauce.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredientSauce.getPrice()).thenReturn(50.0f);

        when(mockIngredientFilling.getName()).thenReturn("cutlet");
        when(mockIngredientFilling.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredientFilling.getPrice()).thenReturn(100.0f);

        when(mockIngredientSauceSecond.getName()).thenReturn("sour cream");
        when(mockIngredientSauceSecond.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredientSauceSecond.getPrice()).thenReturn(30.0f);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testSetBuns() {
        burger.setBuns(mockBun);
        assertSame("Булочка должна быть установлена", mockBun, burger.bun);
    }

    @Test
    public void testAddIngredient() {
        burger.addIngredient(mockIngredientSauce);

        assertEquals("Должен быть добавлен 1 ингредиент", 1, burger.ingredients.size());
        assertSame("Добавленный ингредиент должен соответствовать", mockIngredientSauce, burger.ingredients.get(0));
    }

    @Test
    public void testRemoveIngredient() {
        burger.addIngredient(mockIngredientSauce);
        burger.addIngredient(mockIngredientFilling);

        burger.removeIngredient(0);

        assertEquals("После удаления должен остаться 1 ингредиент", 1, burger.ingredients.size());
        assertSame("Оставшийся ингредиент должен быть вторым", mockIngredientFilling, burger.ingredients.get(0));
    }

    @Test
    public void testMoveIngredient() {
        burger.addIngredient(mockIngredientSauce);
        burger.addIngredient(mockIngredientFilling);
        burger.addIngredient(mockIngredientSauceSecond);
        burger.moveIngredient(0, 2);

        assertEquals("Количество ингредиентов не должно измениться", 3, burger.ingredients.size());
        assertSame("На позиции 0 теперь должен быть второй ингредиент", mockIngredientFilling, burger.ingredients.get(0));
        assertSame("На позиции 1 теперь должен быть третий ингредиент", mockIngredientSauceSecond, burger.ingredients.get(1));
        assertSame("На позиции 2 теперь должен быть первый ингредиент", mockIngredientSauce, burger.ingredients.get(2));
    }

    @Test
    public void testGetPriceWithBunAndIngredients() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientSauce);
        burger.addIngredient(mockIngredientFilling);

        float expectedPrice = (bunPrice * 2) + 50.0f + 100.0f;
        float actualPrice = burger.getPrice();

        assertEquals("Цена должна правильно рассчитываться", expectedPrice, actualPrice, 0.001f);
    }

    @Test
    public void testGetPriceWithOnlyBun() {
        burger.setBuns(mockBun);

        float expectedPrice = bunPrice * 2;
        float actualPrice = burger.getPrice();

        assertEquals("Цена только с булочкой должна быть удвоенной ценой булочки",
                expectedPrice, actualPrice, 0.001f);
    }

    @Test
    public void testGetReceipt() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientSauce);
        burger.addIngredient(mockIngredientFilling);

        String receipt = burger.getReceipt();

        assertNotNull("Чек не должен быть null", receipt);
        assertTrue("Чек должен содержать название булочки", receipt.contains(bunName));
        assertTrue("Чек должен содержать название первого ингредиента", receipt.contains("hot sauce"));
        assertTrue("Чек должен содержать название второго ингредиента", receipt.contains("cutlet"));
        assertTrue("Чек должен содержать тип ингредиента sauce", receipt.contains("sauce"));
        assertTrue("Чек должен содержать тип ингредиента filling", receipt.contains("filling"));
        assertTrue("Чек должен содержать общую цену", receipt.contains("Price:"));
    }

    @Test
    public void testGetReceiptFormat() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientSauce);

        String receipt = burger.getReceipt();
        String[] lines = receipt.split("\n");

        // Проверка формата строк целиком
        assertEquals("Первая строка должна быть в правильном формате",
                String.format("(==== %s ====)", bunName), lines[0].trim());
        assertEquals("Вторая строка должна быть в правильном формате",
                "= sauce hot sauce =", lines[1].trim());
        assertEquals("Третья строка должна быть в правильном формате",
                String.format("(==== %s ====)", bunName), lines[2].trim());
        assertEquals("Четвертая строка должна содержать цену",
                String.format("Price: %f", (bunPrice * 2) + 50.0f), lines[3].trim());
    }

    @Test
    public void testIngredientOrderInReceipt() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredientSauce);
        burger.addIngredient(mockIngredientFilling);

        String receipt = burger.getReceipt();

        int indexIngredient1 = receipt.indexOf("= sauce hot sauce =");
        int indexIngredient2 = receipt.indexOf("= filling cutlet =");

        assertTrue("Ингредиенты должны быть в том же порядке, в котором добавлялись",
                indexIngredient1 < indexIngredient2);
    }
}
