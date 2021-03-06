package io.stackunderflow.flow.ui.web.question;

import io.stackunderflow.flow.application.ServiceRegistry;
import io.stackunderflow.flow.application.question.QuestionFacade;
import io.stackunderflow.flow.application.question.QuestionQuery;
import io.stackunderflow.flow.application.question.QuestionsDTO;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//All questions
@WebServlet(name = "QuestionsPageHandler", urlPatterns = "/questions")
public class QuestionsQueryEndpoint extends HttpServlet {

    @Inject
    private ServiceRegistry serviceRegistry;
    private QuestionFacade questionFacade;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @PostConstruct
    public void postConstruct(){
        questionFacade = serviceRegistry.getQuestionFacade();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Question query is empty, will return all questions
        QuestionsDTO questionsDTO = questionFacade.getQuestions(QuestionQuery.builder().build());
        req.setAttribute("questions", questionsDTO);
        req.getRequestDispatcher("/WEB-INF/views/questions.jsp").forward(req, resp);
    }
}
