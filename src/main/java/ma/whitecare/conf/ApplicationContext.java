package ma.whitecare.conf;

import ma.whitecare.conf.util.PropertiesExtractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ApplicationContext - Fabrique générique centralisée pour gérer les beans (repositories) et la connexion à la base de données.
 * 
 * Cette classe agit comme une fabrique (Factory) qui :
 * - Charge automatiquement tous les beans dont le nom se termine par "Repo" depuis beans.properties
 * - Instancie les repositories par réflexion
 * - Stocke les instances dans un contexte interne
 * - Permet la récupération par nom (String) ou par interface (Class<T>)
 * - Gère la connexion à la base de données via SessionFactory
 * 
 * Pattern utilisé : Singleton (thread-safe avec double-checked locking)
 * 
 * Exemple d'utilisation :
 * <pre>{@code
 * // Récupération par nom
 * PatientRepository repo1 = ApplicationContext.getInstance().getBean("patientRepo");
 * 
 * // Récupération par interface
 * PatientRepository repo2 = ApplicationContext.getInstance().getBean(PatientRepository.class);
 * 
 * // Récupération de la connexion
 * Connection conn = ApplicationContext.getInstance().getConnection();
 * }</pre>
 */
public class ApplicationContext {
    
    private static final String PROPS_PATH = "config/beans.properties";
    private static ApplicationContext instance;
    private final Properties properties;
    
    // Cache des beans par nom
    private final Map<String, Object> beansByName = new ConcurrentHashMap<>();
    
    // Cache des beans par type (interface)
    private final Map<Class<?>, Object> beansByType = new ConcurrentHashMap<>();
    
    // Mapping nom -> type pour la récupération par interface
    private final Map<String, Class<?>> beanNameToType = new ConcurrentHashMap<>();
    
    private final SessionFactory sessionFactory;
    
    /**
     * Constructeur privé pour le pattern Singleton
     */
    private ApplicationContext() {
        this.properties = PropertiesExtractor.loadConfigFile(PROPS_PATH);
        this.sessionFactory = SessionFactory.getInstance();
        
        // Charger automatiquement tous les beans se terminant par "Repo"
        loadAllRepositories();
    }
    
    /**
     * Charge automatiquement tous les beans dont le nom se termine par "Repo"
     */
    private void loadAllRepositories() {
        System.out.println("Chargement automatique des repositories depuis " + PROPS_PATH + "...");
        
        int loadedCount = 0;
        for (String key : properties.stringPropertyNames()) {
            if (key.endsWith("Repo")) {
                try {
                    String className = properties.getProperty(key).trim();
                    
                    // Charger la classe
                    Class<?> implClass = Class.forName(className);
                    
                    // Créer l'instance
                    Object bean = implClass.getDeclaredConstructor().newInstance();
                    
                    // Stocker par nom
                    beansByName.put(key, bean);
                    
                    // Trouver l'interface correspondante
                    Class<?> interfaceClass = findRepositoryInterface(implClass);
                    if (interfaceClass != null) {
                        beansByType.put(interfaceClass, bean);
                        beanNameToType.put(key, interfaceClass);
                    }
                    
                    loadedCount++;
                    System.out.println("  ✓ " + key + " → " + className);
                    
                } catch (Exception e) {
                    System.err.println("  ✗ Erreur lors du chargement de " + key + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("✓ " + loadedCount + " repository(s) chargé(s) avec succès\n");
    }
    
    /**
     * Trouve l'interface Repository correspondante à une classe d'implémentation
     */
    private Class<?> findRepositoryInterface(Class<?> implClass) {
        // Chercher dans les interfaces directement implémentées
        Class<?>[] interfaces = implClass.getInterfaces();
        for (Class<?> iface : interfaces) {
            if (iface.getSimpleName().endsWith("Repository")) {
                return iface;
            }
        }
        
        // Chercher dans les superclasses
        Class<?> superClass = implClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            Class<?> found = findRepositoryInterface(superClass);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    /**
     * Retourne l'instance unique du contexte applicatif (Singleton)
     */
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
     * Récupère un bean par son nom depuis le contexte.
     * Si le bean n'existe pas encore, il est créé et mis en cache.
     * 
     * @param beanName le nom du bean dans beans.properties
     * @return l'instance du bean
     * @throws IllegalStateException si le bean n'est pas trouvé
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        // Vérifier si le bean est déjà en cache
        if (beansByName.containsKey(beanName)) {
            return (T) beansByName.get(beanName);
        }
        
        // Récupérer le nom de classe depuis les propriétés
        String className = properties.getProperty(beanName);
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalStateException("Bean '" + beanName + "' non trouvé dans " + PROPS_PATH);
        }
        
        // Créer l'instance via réflexion
        try {
            Class<?> clazz = Class.forName(className.trim());
            Object bean = clazz.getDeclaredConstructor().newInstance();
            
            // Mettre en cache
            beansByName.put(beanName, bean);
            
            // Trouver et stocker l'interface
            Class<?> interfaceClass = findRepositoryInterface(clazz);
            if (interfaceClass != null) {
                beansByType.put(interfaceClass, bean);
                beanNameToType.put(beanName, interfaceClass);
            }
            
            return (T) bean;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du bean '" + beanName + 
                    "' de classe '" + className + "'", e);
        }
    }
    
    /**
     * Récupère un bean par son type (interface) depuis le contexte.
     * 
     * @param beanType la classe de l'interface du repository
     * @return l'instance du bean correspondant à cette interface
     * @throws IllegalStateException si aucun bean n'est trouvé pour ce type
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanType) {
        // Vérifier si le bean est déjà en cache par type
        if (beansByType.containsKey(beanType)) {
            return (T) beansByType.get(beanType);
        }
        
        // Chercher dans tous les beans chargés
        for (Map.Entry<String, Object> entry : beansByName.entrySet()) {
            Object bean = entry.getValue();
            if (beanType.isInstance(bean)) {
                // Mettre en cache pour les prochaines fois
                beansByType.put(beanType, bean);
                return (T) bean;
            }
        }
        
        throw new IllegalStateException("Aucun bean de type '" + beanType.getName() + 
                "' trouvé dans le contexte. Vérifiez que le repository est configuré dans " + PROPS_PATH);
    }
    
    /**
     * Retourne tous les noms de beans chargés
     */
    public Set<String> getBeanNames() {
        return new HashSet<>(beansByName.keySet());
    }
    
    /**
     * Retourne tous les types (interfaces) de beans chargés
     */
    public Set<Class<?>> getBeanTypes() {
        return new HashSet<>(beansByType.keySet());
    }
    
    /**
     * Vérifie si un bean existe par son nom
     */
    public boolean containsBean(String beanName) {
        return beansByName.containsKey(beanName) || properties.containsKey(beanName);
    }
    
    /**
     * Vérifie si un bean existe par son type
     */
    public boolean containsBean(Class<?> beanType) {
        return beansByType.containsKey(beanType) || 
               beansByName.values().stream().anyMatch(beanType::isInstance);
    }
    
    /**
     * ============================================
     * MÉTHODES POUR LA GESTION DE LA CONNEXION
     * ============================================
     */
    
    /**
     * Obtient une connexion à la base de données.
     * Cette méthode doit être appelée dans chaque repository avant d'exécuter une requête.
     * 
     * <p>Exemple d'utilisation dans un repository :</p>
     * <pre>{@code
     * public List<Patient> findAll() {
     *     String sql = "SELECT * FROM patient";
     *     try (Connection c = ApplicationContext.getInstance().getConnection();
     *          PreparedStatement ps = c.prepareStatement(sql);
     *          ResultSet rs = ps.executeQuery()) {
     *         // Traitement des résultats...
     *     } catch (SQLException e) {
     *         throw new RuntimeException(e);
     *     }
     * }
     * }</pre>
     * 
     * @return une connexion JDBC valide
     * @throws SQLException en cas d'erreur de connexion
     */
    public Connection getConnection() throws SQLException {
        return sessionFactory.getConnection();
    }
    
    /**
     * Ferme proprement la connexion à la base de données.
     * À appeler à la fermeture de l'application.
     */
    public void closeConnection() {
        sessionFactory.closeConnection();
    }
    
    /**
     * Vérifie si la connexion à la base de données est valide.
     * 
     * @return true si la connexion est valide, false sinon
     */
    public boolean isConnectionValid() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}
