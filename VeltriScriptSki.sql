-- Supprimer les tables dans l'ordre inverse des dépendances
DROP TABLE LessonSession CASCADE CONSTRAINTS;
DROP TABLE Booking CASCADE CONSTRAINTS;
DROP TABLE Instructor CASCADE CONSTRAINTS;
DROP TABLE InstructorAccreditation CASCADE CONSTRAINTS;
DROP TABLE Lesson CASCADE CONSTRAINTS;
DROP TABLE Period CASCADE CONSTRAINTS;
DROP TABLE LessonType CASCADE CONSTRAINTS;
DROP TABLE Accreditation CASCADE CONSTRAINTS;
DROP TABLE Skier CASCADE CONSTRAINTS;
DROP TABLE person CASCADE CONSTRAINTS;

-- Supprimer les séquences
DROP SEQUENCE booking_seq;
DROP SEQUENCE period_seq;
DROP SEQUENCE lesson_seq;
DROP SEQUENCE lessonType_seq;
DROP SEQUENCE accreditation_seq;
DROP SEQUENCE instructor_seq;
DROP SEQUENCE skier_seq;
DROP SEQUENCE person_seq;
DROP SEQUENCE lessonSession_seq;



-- Création des séquences
CREATE SEQUENCE person_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE skier_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE instructor_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE accreditation_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE lessonType_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE lesson_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE period_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE booking_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE lessonSession_seq START WITH 1 INCREMENT BY 1 NOCACHE;


-- Création des tables
CREATE TABLE Person(
   id_Person NUMBER(10),
   firstName VARCHAR2(50),
   lastName VARCHAR2(50),
   Birthdate DATE,
   PRIMARY KEY(id_Person)
);

CREATE TABLE Skier(
   id_skier NUMBER(10),
   skier_phoneNumber VARCHAR2(50) NOT NULL,
   skier_email VARCHAR2(50),
   id_Person NUMBER(10) NOT NULL,
   PRIMARY KEY(id_skier),
   UNIQUE(skier_phoneNumber),
   UNIQUE(skier_email),
   FOREIGN KEY(id_Person) REFERENCES Person(id_Person)
);

CREATE TABLE Instructor(
   id_instructor NUMBER(10),
   instructor_hireDate DATE,
   id_Person NUMBER(10) NOT NULL,
   PRIMARY KEY(id_instructor),
   FOREIGN KEY(id_Person) REFERENCES Person(id_Person)
);

CREATE TABLE Accreditation(
   id_accreditation NUMBER(10),
   accreditation_name VARCHAR2(50),
   PRIMARY KEY(id_accreditation)
);

CREATE TABLE LessonType(
   id_lessonType NUMBER(10),
   lesson_level VARCHAR2(50),
   lesson_price NUMBER(10,2),
   id_accreditation NUMBER(10) NOT NULL,
   PRIMARY KEY(id_lessonType),
   FOREIGN KEY(id_accreditation) REFERENCES Accreditation(id_accreditation)
);



CREATE TABLE Lesson(
   id_lesson NUMBER(10),
   lessonDate TIMESTAMP,
   minBookings NUMBER(10),
   maxBookings NUMBER(10),
   isCollective NUMBER(1),
   nb_hours NUMBER(10),
   id_lessonType NUMBER(10) NOT NULL,
   id_instructor NUMBER(10) NOT NULL,
   PRIMARY KEY(id_lesson),
   FOREIGN KEY(id_lessonType) REFERENCES LessonType(id_lessonType),
   FOREIGN KEY(id_instructor) REFERENCES Instructor(id_instructor)
);

CREATE TABLE Period(
   id_period NUMBER(10),
   startDate DATE,
   endDate DATE,
   isVacation NUMBER(1),
   PRIMARY KEY(id_period)
);


CREATE TABLE Booking(
   id_booking NUMBER(10),
   reservation_date DATE,
   insurance_opt NUMBER(1),
   id_lesson NUMBER(10) NOT NULL,
   id_period NUMBER(10) NOT NULL,
   id_skier NUMBER(10) NOT NULL,
   PRIMARY KEY(id_booking),
   FOREIGN KEY(id_lesson) REFERENCES Lesson(id_lesson),
   FOREIGN KEY(id_period) REFERENCES Period(id_period),
   FOREIGN KEY(id_skier) REFERENCES Skier(id_skier)
);

CREATE TABLE LessonSession(
   id_session_ NUMBER(10),
   session_type VARCHAR2(50),
   id_booking NUMBER(10) NOT NULL,
   PRIMARY KEY(id_session_),
   FOREIGN KEY(id_booking) REFERENCES Booking(id_booking)
);



CREATE TABLE InstructorAccreditation(
   id_instructor NUMBER(10),
   id_accreditation NUMBER(10),
   PRIMARY KEY(id_instructor, id_accreditation),
   FOREIGN KEY(id_instructor) REFERENCES Instructor(id_instructor),
   FOREIGN KEY(id_accreditation) REFERENCES Accreditation(id_accreditation)
);

-- Insertion des données

-- Insertion des données dans Person et Instructor
INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Alice', 'Davis', TO_DATE('1990-03-12', 'YYYY-MM-DD'));

INSERT INTO Instructor (id_instructor, instructor_hireDate, id_Person) 
VALUES (instructor_seq.NEXTVAL, TO_DATE('2015-06-01', 'YYYY-MM-DD'), person_seq.CURRVAL);

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'David', 'Wilson', TO_DATE('1987-08-20', 'YYYY-MM-DD'));

INSERT INTO Instructor (id_instructor, instructor_hireDate, id_Person) 
VALUES (instructor_seq.NEXTVAL, TO_DATE('2016-09-15', 'YYYY-MM-DD'), person_seq.CURRVAL);

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Laura', 'Taylor', TO_DATE('1993-12-05', 'YYYY-MM-DD'));

INSERT INTO Instructor (id_instructor, instructor_hireDate, id_Person) 
VALUES (instructor_seq.NEXTVAL, TO_DATE('2017-01-10', 'YYYY-MM-DD'), person_seq.CURRVAL);

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Chris', 'Anderson', TO_DATE('1984-11-17', 'YYYY-MM-DD'));

INSERT INTO Instructor (id_instructor, instructor_hireDate, id_Person) 
VALUES (instructor_seq.NEXTVAL, TO_DATE('2014-04-25', 'YYYY-MM-DD'), person_seq.CURRVAL);

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Sophie', 'Martinez', TO_DATE('1982-06-30', 'YYYY-MM-DD'));

INSERT INTO Instructor (id_instructor, instructor_hireDate, id_Person) 
VALUES (instructor_seq.NEXTVAL, TO_DATE('2018-11-20', 'YYYY-MM-DD'), person_seq.CURRVAL);

-- Insertion des données dans Accreditation
INSERT INTO Accreditation (id_accreditation, accreditation_name) 
VALUES (accreditation_seq.NEXTVAL, 'Ski Adulte');

INSERT INTO Accreditation (id_accreditation, accreditation_name) 
VALUES (accreditation_seq.NEXTVAL, 'Ski Enfant');

INSERT INTO Accreditation (id_accreditation, accreditation_name) 
VALUES (accreditation_seq.NEXTVAL, 'Snowboard Enfant');

INSERT INTO Accreditation (id_accreditation, accreditation_name) 
VALUES (accreditation_seq.NEXTVAL, 'Snowboard Adulte');

INSERT INTO Accreditation (id_accreditation, accreditation_name) 
VALUES (accreditation_seq.NEXTVAL, 'Télémark');

INSERT INTO Accreditation (id_accreditation, accreditation_name) 
VALUES (accreditation_seq.NEXTVAL, 'Ski de fond');


-- Insertion des données dans LessonType

--Ski enfant
INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Petit Spirou', 120.00, 2);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Bronze', 140.00, 2);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Argent', 140.00, 2);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Or', 140.00, 2);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Platine', 140.00, 2);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Compétition', 160.00, 2);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Hors-piste', 160.00, 2);

--Snowboard enfant

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, '1', 130.00, 3);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, '2 à 4', 150.00, 3);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Hors-piste', 150.00, 3);

--Ski adulte

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'De 1 à 4', 150.00, 1);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Hors-piste', 170.00, 1);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Compétition', 170.00, 1);

--Snowboard Adulte

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'De 1 à 4', 150.00, 4);

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'Hors-piste', 170.00, 4);

--Télémark

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'De 1 à 4', 140.00, 5);

--Ski de fond

INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price, id_accreditation) 
VALUES (lessonType_seq.NEXTVAL, 'De 1 à 4', 120.00, 6);


-- Insertion des données dans Lesson
INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor) 
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2024-12-06 00:00:00', 5, 8, 168, 1, 12, 1);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor) 
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2024-12-07 00:00:00', 1, 4, 2, 0, 1, 2);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor) 
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2024-12-08 00:00:00', 5, 8, 168, 1, 9, 3);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2024-12-09 00:00:00', 6, 10, 168, 1, 11, 4); 

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2024-12-10 00:00:00', 1, 4, 2, 0, 2, 2);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2025-02-25 00:00:00', 5, 8, 168, 1, 1, 5);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2025-03-13 00:00:00', 1, 4, 1, 0, 12, 4);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2025-03-23 00:00:00', 5, 8, 168, 1, 15, 4);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2025-02-16 00:00:00', 1, 4, 2, 0, 2, 2);

INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, nb_hours, isCollective, id_lessonType, id_instructor)
VALUES (lesson_seq.NEXTVAL, TIMESTAMP '2025-03-23 00:00:00', 1, 4, 1, 0, 3, 2);

-- Insertion des données dans Period
-- Hors vacances scolaires
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2024-12-06', 'YYYY-MM-DD'), TO_DATE('2024-12-20', 'YYYY-MM-DD'), 0);

-- Vacances de Noël/Nouvel an
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2024-12-21', 'YYYY-MM-DD'), TO_DATE('2025-01-04', 'YYYY-MM-DD'), 1);

-- Hors vacances scolaires
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2025-01-05', 'YYYY-MM-DD'), TO_DATE('2025-02-28', 'YYYY-MM-DD'), 0);

-- Vacances de Carnaval
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2025-03-01', 'YYYY-MM-DD'), TO_DATE('2025-03-08', 'YYYY-MM-DD'), 1);

-- Hors vacances scolaires
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2025-03-09', 'YYYY-MM-DD'), TO_DATE('2025-04-11', 'YYYY-MM-DD'), 0);

-- Vacances de Pâques
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2025-04-12', 'YYYY-MM-DD'), TO_DATE('2025-04-26', 'YYYY-MM-DD'), 1);

-- Fin de saison
INSERT INTO Period (id_period, startDate, endDate, isVacation) 
VALUES (period_seq.NEXTVAL, TO_DATE('2025-04-27', 'YYYY-MM-DD'), TO_DATE('2025-05-03', 'YYYY-MM-DD'), 0);


-- Insertion des données dans InstructorAccreditation
INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (1, 1);
INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (1, 6);

INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (2, 2);
INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (2, 3);
INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (2, 6);

INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (3, 3);

INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (4, 1);
INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (4, 4);

INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (5, 2);
INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) 
VALUES (5, 5);


-- Insertion des données dans Person et Skier
INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'John', 'Doe', TO_DATE('2019-05-15', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Jane', 'Smith', TO_DATE('1997-11-30', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Robert', 'Brown', TO_DATE('1978-04-22', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Emily', 'Johnson', TO_DATE('2016-09-10', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Michael', 'Williams', TO_DATE('1995-07-25', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Léo', 'Martin', TO_DATE('2015-05-12', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Alice', 'Girard', TO_DATE('2016-08-20', 'YYYY-MM-DD'));

INSERT INTO Person (id_Person, firstName, lastName, Birthdate) 
VALUES (person_seq.NEXTVAL, 'Antoine', 'Leclerc', TO_DATE('2019-01-28', 'YYYY-MM-DD'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+32491234567', 'john.doe@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'John' AND lastName = 'Doe'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+32456789641', 'jane.smith@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Jane' AND lastName = 'Smith'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+324965712345', 'robert.brown@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Robert' AND lastName = 'Brown'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+324569781243', 'emily.johnson@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Emily' AND lastName = 'Johnson'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+32456794531', 'michael.williams@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Michael' AND lastName = 'Williams'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)   
VALUES (skier_seq.NEXTVAL, '+32498786512', 'leo.martin@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Léo' AND lastName = 'Martin'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+324597123655', 'alice.girard@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Alice' AND lastName = 'Girard'));

INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person)
VALUES (skier_seq.NEXTVAL, '+3289475611', 'antoine.leclerc@example.com', 
        (SELECT id_Person FROM Person WHERE firstName = 'Antoine' AND lastName = 'Leclerc'));

-- Insertion des données dans Booking
INSERT INTO Booking (id_booking, reservation_date, insurance_opt, id_lesson, id_period, id_skier)
VALUES (booking_seq.NEXTVAL, TO_DATE('2024-09-10', 'YYYY-MM-DD'), 0, 10, 3, 1);

INSERT INTO Booking (id_booking, reservation_date, insurance_opt, id_lesson, id_period, id_skier)
VALUES (booking_seq.NEXTVAL, TO_DATE('2024-10-14', 'YYYY-MM-DD'), 1, 10, 3, 8); 

INSERT INTO Booking (id_booking, reservation_date, insurance_opt, id_lesson, id_period, id_skier)
VALUES (booking_seq.NEXTVAL, TO_DATE('2024-10-20', 'YYYY-MM-DD'), 1, 6, 3, 1); 

INSERT INTO Booking (id_booking, reservation_date, insurance_opt, id_lesson, id_period, id_skier)
VALUES (booking_seq.NEXTVAL, TO_DATE('2024-10-20', 'YYYY-MM-DD'), 1, 8, 5, 2); 

INSERT INTO Booking (id_booking, reservation_date, insurance_opt, id_lesson, id_period, id_skier)
VALUES (booking_seq.NEXTVAL, TO_DATE('2024-10-20', 'YYYY-MM-DD'), 1, 6, 3, 8); 
 
-- Insertion de données dans LessonSession
Insert into LessonSession (id_session_,session_type,id_booking) 
VALUES (booking_seq.NEXTVAL,'Morning',1);
VALUES (booking_seq.NEXTVAL,'Afternoon',1);
VALUES (booking_seq.NEXTVAL,'Morning',1);
VALUES (booking_seq.NEXTVAL,'Morning',1);
VALUES (booking_seq.NEXTVAL,'Morning',1);
VALUES (booking_seq.NEXTVAL,'Morning',1);
VALUES (booking_seq.NEXTVAL,'Morning',2);
VALUES (booking_seq.NEXTVAL,'Afternoon',2);
VALUES (booking_seq.NEXTVAL,'Morning',2);
VALUES (booking_seq.NEXTVAL,'Morning',2);
VALUES (booking_seq.NEXTVAL,'Morning',2);
VALUES (booking_seq.NEXTVAL,'Morning',2);
VALUES (booking_seq.NEXTVAL,'Morning',3);
VALUES (booking_seq.NEXTVAL,'Morning',3);
VALUES (booking_seq.NEXTVAL,'Morning',3);
VALUES (booking_seq.NEXTVAL,'Morning',3);
VALUES (booking_seq.NEXTVAL,'Morning',3);
VALUES (booking_seq.NEXTVAL,'Morning',3);
VALUES (booking_seq.NEXTVAL,'Morning',4);
VALUES (booking_seq.NEXTVAL,'Morning',4);
VALUES (booking_seq.NEXTVAL,'Afternoon',4);
VALUES (booking_seq.NEXTVAL,'Morning',4);
VALUES (booking_seq.NEXTVAL,'Morning',4);
VALUES (booking_seq.NEXTVAL,'Morning',4);



GRANT SELECT, INSERT, UPDATE, DELETE ON Person TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON Skier TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON Instructor TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON Accreditation TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON LessonType TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON Lesson TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON Period TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON Booking TO STUDENT03_20;
GRANT SELECT, INSERT, UPDATE, DELETE ON LessonSession TO STUDENT03_20;


GRANT SELECT ON skier_seq TO STUDENT03_20;
GRANT SELECT ON instructor_seq TO STUDENT03_20;
GRANT SELECT ON accreditation_seq TO STUDENT03_20;
GRANT SELECT ON lessonType_seq TO STUDENT03_20;
GRANT SELECT ON lesson_seq TO STUDENT03_20;
GRANT SELECT ON period_seq TO STUDENT03_20;
GRANT SELECT ON booking_seq TO STUDENT03_20;
GRANT SELECT ON lessonSession_seq TO STUDENT03_20;


SELECT * FROM accreditation;
SELECT * FROM booking;
SELECT * FROM instructor;
SELECT * FROM instructoraccreditation;
SELECT * FROM lesson;
SELECT * FROM lessontype;
SELECT * FROM period;
SELECT * FROM skier;
SELECT * FROM person;
SELECT * FROM LessonSession;
                     
commit;