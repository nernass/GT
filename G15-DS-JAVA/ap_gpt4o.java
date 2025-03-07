import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private ComponentB componentB;

    @InjectMocks
    private ComponentA componentA;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidInputSuccessPath() {
        // Arrange
        String username = "validUser";
        String email = "valid@example.com";
        String password = "validPassword";
        when(componentB.saveToDatabase(username, email, password)).thenReturn(true);

        // Act
        boolean isValid = componentA.validateInput(username, email, password);
        boolean isSaved = componentB.saveToDatabase(username, email, password);

        // Assert
        assertTrue(isValid);
        assertTrue(isSaved);
        verify(componentB, times(1)).saveToDatabase(username, email, password);
    }

    @Test
    public void testComponentBFailure() {
        // Arrange
        String username = "validUser";
        String email = "valid@example.com";
        String password = "validPassword";
        when(componentB.saveToDatabase(username, email, password)).thenReturn(false);

        // Act
        boolean isValid = componentA.validateInput(username, email, password);
        boolean isSaved = componentB.saveToDatabase(username, email, password);

        // Assert
        assertTrue(isValid);
        assertFalse(isSaved);
        verify(componentB, times(1)).saveToDatabase(username, email, password);
    }

    @Test
    public void testInvalidInput() {
        // Arrange
        String username = "us";
        String email = "invalidemail";
        String password = "short";

        // Act
        boolean isValid = componentA.validateInput(username, email, password);

        // Assert
        assertFalse(isValid);
        verify(componentB, never()).saveToDatabase(anyString(), anyString(), anyString());
    }
}