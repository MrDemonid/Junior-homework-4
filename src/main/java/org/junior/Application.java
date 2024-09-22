package org.junior;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junior.models.Person;
import org.junior.models.Phone;

import java.time.LocalDate;
import java.util.List;

public class Application {

    private SessionFactory factory;

    public Application() {
        setup();
    }

    public void run()
    {
        /*
            Добавляем в БД несколько объектов Person
            Create
         */
        addPersonsToDB();

        /*
            Выводим список всех Person в БД
            Read
         */
        showDb();

        /*
            Модифицируем первого Person из списка, добавив ему телефон
            Update
         */
        System.out.println("Update person...");
        List<Person> persons = getAllPersons();
        Person person = getPersonById(persons.getFirst().getId());
        person.addPhone(new Phone("8-927-273-23-12", person));
        person = updatePerson(person);      // исходный объект не меняется, поэтому нужно использовать возвращенный merge()
        showDb();

        /*
            Удаляем объект Person и связанные с ним Phone из БД.
            Delete
         */
        System.out.println("Delete person...");
        deletePerson(person);
        showDb();


        /*
            Удаляем всех остальных Person из БД
         */
        persons = getAllPersons();
        for (Person p : persons) {
            deletePerson(p);
        }

        close();
    }

    /**
     * Извлекает с БД все записи Person + их телефоны
     */
    public List<Person> getAllPersons()
    {
        try (EntityManager manager = factory.createEntityManager())
        {
            return manager.createQuery("FROM Person", Person.class).getResultList();
        }
    }

    /**
     * Извлекает из БД объект Person по его ID
     */
    public Person getPersonById(long id)
    {
        try (EntityManager manager = factory.createEntityManager())
        {
            Query query = manager.createQuery("FROM Person WHERE id = :id", Person.class);
            query.setParameter("id", id);
            return  (Person) query.getSingleResult();
        }
    }

    /**
     * Модифицирует находящийся в БД объект Person
     */
    public Person updatePerson(Person person)
    {
        if (person.getId() == 0L)
            return null;                        // похоже объект в состоянии Transient
        EntityTransaction transaction = null;
        try (EntityManager manager = factory.createEntityManager())
        {
            manager.detach(person);             // переводим объект в состояние Detached
            transaction = manager.getTransaction();
            transaction.begin();
            Person res = manager.merge(person); // обратно в состояние Managed
            transaction.commit();               // выполнит flush(), обновляя объект в БД
            return res;
        } catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            throw e;
        }

    }

    /**
     * Добавление в БД объекта Person + его телефоны
     */
    public void addPerson(Person person, Phone ... phones)
    {
        EntityTransaction transaction = null;
        try (EntityManager manager = factory.createEntityManager())
        {
            transaction = manager.getTransaction();
            transaction.begin();
            for (Phone phone : phones)
            {
                person.addPhone(phone);
            }
            manager.persist(person);
            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            throw e;
        }
    }

    /**
     * Удаляет Person и его связи из БД
     */
    public void deletePerson(Person person)
    {
        if (person.getId() == 0L)
            return;                             // похоже объект в состоянии Transient
        EntityTransaction transaction = null;
        try (EntityManager manager = factory.createEntityManager())
        {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.remove(person);
            transaction.commit();
        } catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            throw e;
        }
    }

    private void addPersonsToDB()
    {
        addPerson(new Person("Hlus", "Andrey", "Alexandrovich", LocalDate.of(1975, 8, 27)),
                new Phone("+7-902-204-80-09"),
                new Phone("+7-953-445-28-05")
        );
        addPerson(new Person("Trubanov", "Ivan", "Alexandrovich", LocalDate.of(1975, 9, 6)),
                new Phone("+7-906-283-62-11")
        );
    }

    /**
     * Настройка подключения к БД
     */
    private void setup()
    {
        final StandardServiceRegistry registry;
//        registry = new StandardServiceRegistryBuilder().build();                // читаем конфиг из hibernate.properties

        registry = new StandardServiceRegistryBuilder().configure().build();  // читаем конфиг из hibernate.cfg.xml
        try {
            factory = new MetadataSources(registry)
//                    .addAnnotatedClass(Person.class)              // добавляем аннотированные классы
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e)
        {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private void close()
    {
        if (factory != null)
            factory.close();
    }

    private void showDb()
    {
        System.out.println("Database: ");
        getAllPersons().forEach(System.out::println);   // выводим список всех Person в БД
        System.out.println();
    }

}
