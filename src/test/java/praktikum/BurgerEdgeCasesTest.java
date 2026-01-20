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

    public BurgerEdgeCasesTest(float bunPrice) {
        this.bunPrice = bunPrice;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {0.0f},     // Бесплатная булочка
                {1.5f},     // Дробная цена
                {1000.0f}   // Высокая цена
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
        burger.addIngredient(mockIngredient);
        burger.addIngredient(mockIngredient);
        burger.addIngredient(mockIngredient);

        assertEquals("Должно быть 3 ингредиента", 3, burger.ingredients.size());

        burger.removeIngredient(1);

        assertEquals("После удаления должно остаться 2 ингредиента", 2, burger.ingredients.size());

        float expectedPrice = (bunPrice * 2) + (10.0f * 2);
        float actualPrice = burger.getPrice();

        assertEquals("Цена после удаления ингредиента", expectedPrice, actualPrice, 0.001f);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientInvalidIndex() {
        burger.addIngredient(mockIngredient);
        burger.removeIngredient(5); // Неверный индекс
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientInvalidIndex() {
        burger.addIngredient(mockIngredient);
        burger.moveIngredient(0, 5); // Неверный индекс
    }

    @Test
    public void testEmptyBurgerReceipt() {
        burger.setBuns(mockBun);

        String receipt = burger.getReceipt();

        assertNotNull("Чек пустого бургера не должен быть null", receipt);
        assertTrue("Чек должен содержать название булочки", receipt.contains("test bun"));
        assertTrue("Чек должен содержать цену", receipt.contains("Price:"));

        // Проверяем, что нет строк с ингредиентами
        String[] lines = receipt.split("\n");
        assertEquals("Чек пустого бургера должен иметь определенное количество строк", 4, lines.length);
    }
}