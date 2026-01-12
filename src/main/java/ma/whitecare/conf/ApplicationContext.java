package ma.whitecare.conf;

import ma.whitecare.conf.util.PropertiesExtractor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ApplicationContext - Fabrique centralisée pour gérer les beans et la
 * connexion à la base de données.
 */
public class ApplicationContext {

    private static final String PROPS_PATH = "config/beans.properties";
    private static ApplicationContext instance;
    private final Properties properties;

    // Cache des beans
    private final Map<String, Object> beansByName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> beansByType = new ConcurrentHashMap<>();

    private final SessionFactory sessionFactory;

    private ApplicationContext() {
        this.properties = PropertiesExtractor.loadConfigFile(PROPS_PATH);
        this.sessionFactory = SessionFactory.getInstance();
        loadAllBeans();
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            synchronized (ApplicationContext.class) {
                if (instance == null) {
                    instance = new ApplicationContext();
                }
            }
        }
        return instance;
    }

    /**
     * Charge tous les beans définis dans beans.properties
     */
    private void loadAllBeans() {
        for (String key : properties.stringPropertyNames()) {
            try {
                String className = properties.getProperty(key).trim();
                Class<?> clazz = Class.forName(className);

                // Pour l'instant, on instancie via constructeur sans argument (Repositories)
                // On pourrait ajouter de la logique pour les Services (injection)
                Object bean = clazz.getDeclaredConstructor().newInstance();

                beansByName.put(key, bean);

                // Enregistrement par interface si possible
                for (Class<?> iface : clazz.getInterfaces()) {
                    beansByType.put(iface, bean);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement bean '" + key + "': " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        return (T) beansByName.get(beanName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanClass) {
        return (T) beansByType.get(beanClass);
    }

    public Set<String> getBeanNames() {
        return new HashSet<>(beansByName.keySet());
    }

    public Connection getConnection() throws SQLException {
        return sessionFactory.getConnection();
    }

    public void closeConnection() {
        sessionFactory.closeConnection();
    }
}
