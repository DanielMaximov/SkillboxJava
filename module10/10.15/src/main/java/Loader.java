import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pojo.Course;
import pojo.PurchaseList;
import pojo.Student;
import pojo.StudentsCourses;
import pojo.Subscription;
import pojo.Teacher;

import java.util.Date;
import java.util.List;

public class Loader {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Course.class)
                .addAnnotatedClass(PurchaseList.class)
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Subscription.class)
                .addAnnotatedClass(Teacher.class)
                .addAnnotatedClass(StudentsCourses.class)
                .buildSessionFactory();

        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            List<Object[]> purchaseLists = session.createQuery("SELECT s.id, c.id, p.price,  p.subscriptionDate " +
                    "FROM PurchaseList p " +
                    "join Student s on (s.name = p.studentName) " +
                    "join Course c on (c.name = p.courseName)", Object[].class).getResultList();
                 session.getTransaction().commit();
              purchaseLists.stream().forEach(row -> {
                  StudentsCourses studentsCourses = StudentsCourses.builder()
                          .id(new StudentsCourses.Id((int) row[0], (int) row [1]))
                          .student(null)
                          .course(null)
                          .price((int) row [2])
                          .subscriptionDate((Date) row[3])
                          .build();
                  session.save(studentsCourses);
              });
            session.getTransaction().commit();
        } finally {
            factory.close();
        }
    }
}
