package io.stackunderflow.flow.infrastructure.persistence.jdbc;

import io.stackunderflow.flow.application.identitymgmt.login.RegistrationFailedException;
import io.stackunderflow.flow.application.question.QuestionQuery;
import io.stackunderflow.flow.domain.question.IQuestionRepository;
import io.stackunderflow.flow.domain.question.Question;
import io.stackunderflow.flow.domain.question.QuestionId;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@ApplicationScoped
@Named("JdbcQuestionRepository")
public class JdbcQuestionRepository extends JdbcRepository implements IQuestionRepository {

    public JdbcQuestionRepository(){}

    @Override
    public Collection<Question> find(QuestionQuery query) {
        Collection<Question> questions = new LinkedList<>();
        //Find by id
        if(query.getId() != null){
            Optional<Question> q = findById(query.getId());
            if(!q.isEmpty()) {
               questions.add(q.get());
            }else{
                //TODO : throw error ? question id not found ?
            }
            return questions;
        }
        //if the query is empty, return the all the questions
        questions = findAll();
        return questions;
    }

    @Override
    public void save(Question entity) throws RegistrationFailedException {

        try {
            String query = "INSERT INTO question (id, title, text, author) VALUES(?, ?, ?, ?)";
            super.executeInsertQuery(query, entity.getId().asString(), entity.getTitle(), entity.getText(), entity.getAuthor());

        }catch(RegistrationFailedException e){
            throw new RegistrationFailedException(e.getMessage());
        }
    }

    @Override
    public void remove(QuestionId id) {

    }

    @Override
    public Optional<Question> findById(QuestionId id) {

        ResultSet rs = super.fetchData("SELECT * FROM question WHERE id = ?", id.asString());
        Question q = null;

        try{
            if(rs.next()) {
                String title = rs.getString(2);
                String text = rs.getString(3);
                String author = rs.getString(5);
                q = Question.builder()
                        .id(id)
                        .title(title)
                        .text(text)
                        .author(author)
                        .build();
            }
        }catch(SQLException e){
            System.out.println("error : " + e.getMessage());
        }
        if(q == null)
            return Optional.empty();
        else
            return Optional.of(q);
    }

    @Override
    public Collection<Question> findAll() {
        Collection<Question> allQuestion = new LinkedList<>();

        ResultSet rs = super.fetchData("SELECT * FROM question");

        try{
            while(rs.next()) {
                QuestionId id = new QuestionId(rs.getString(1));
                String title = rs.getString(2);
                String text = rs.getString(3);
                String author = rs.getString(4);

                Question q = Question.builder()
                        .id(id)
                        .title(title)
                        .text(text)
                        .author(author)
                        .build();

                allQuestion.add(q);
            }
        }catch(SQLException e){
            System.out.println("error : " + e.getMessage());
        }
        return allQuestion;
    }
}
