import org.bukkit.Registry;
import org.bukkit.Keyed;
import org.bukkit.Material;

public class TestRegistry {
    public static void main(String[] args) {
        System.out.println("Registry is assignable from Registry.BLOCK? " + Registry.class.isAssignableFrom(Registry.class));
        System.out.println("Material implements Keyed? " + Keyed.class.isAssignableFrom(Material.class));
    }
}
