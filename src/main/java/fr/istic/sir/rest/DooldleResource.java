package fr.istic.sir.rest;

import fr.istic.sir.entities.*;
import fr.istic.sir.entities.repository.SurveyRepository;
import fr.istic.sir.entities.repository.UserRepository;
import fr.istic.sir.repositories.Repository;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

@Path("/doodle")
public class DooldleResource {
    private Repository<User> repository;
    private Repository<Survey> surveyRepository;

    public DooldleResource() {
        this.repository = new UserRepository();
        this.surveyRepository = new SurveyRepository();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User store(JSONObject request) throws Exception {
        JSONObject u = request.getJSONObject("user");
        Optional<User> opt = repository.findById(u.getString("email"));
        User user;
        if (opt.isPresent())
            user = opt.get();
        else
            user = new User(u.getString("email"), u.getString("lastName"), u.getString("firstName"));

        JSONObject m = request.getJSONObject("meeting");
        Meeting meeting = new Meeting(m.getString("title"), m.getString("summary"));
        if (request.has("pause")) {
            boolean pause = request.getBoolean("pause");
            meeting.setPause(pause);
        }

        user.setMeetings(Collections.singletonList(meeting));
        meeting.setCreator(user);

        int surveyType = request.getInt("surveyType");
        Survey survey;
        JSONArray locations, dates;
        switch (surveyType) {
            case 0: // Date
                survey = new DateSurvey();
                ((DateSurvey) survey).setDates(associateDate(survey, request.getJSONArray("dates")));
                break;

            case 1: // Location
                survey = new AddressSurvey();
                ((AddressSurvey) survey).setAddresses(associateLocation(survey, request.getJSONArray("locations")));
                break;

            case 2: // Date and Location
                survey = new AddressAndDateSurvey();

                dates = request.getJSONArray("dates");
                ((AddressAndDateSurvey) survey).setDates(associateDate(survey, dates));

                locations = request.getJSONArray("locations");
                ((AddressAndDateSurvey) survey).setAddresses(associateLocation(survey, locations));
                break;

            default:
                return null;
        }
        meeting.setSurveys(Collections.singletonList(survey));
        survey.setMeeting(meeting);

        repository.save(user);

        // update survey link
        survey.setLink("/api/surveys/" + survey.getId());
        surveyRepository.update(survey);

        return user;
    }

    /**
     * @param survey    Survey to associate to
     * @param locations Locations to associate to
     * @throws JSONException JSONException
     */
    private List<Address> associateLocation(Survey survey, JSONArray locations) throws JSONException {
        List<Address> l = new ArrayList<>();
        for (int i = 0; i < locations.length(); ++i) {
            Address address = new Address(locations.getString(i));
            l.add(address);
            address.setSurvey(survey);
        }

        return l;
    }

    /**
     * @param survey Survey to associate to
     * @param dates  Locations to associate to
     * @throws JSONException JSONException
     */
    private List<fr.istic.sir.entities.Date> associateDate(Survey survey, JSONArray dates) throws JSONException {
        List<fr.istic.sir.entities.Date> l = new ArrayList<>();
        for (int i = 0; i < dates.length(); ++i) {
            fr.istic.sir.entities.Date d = new fr.istic.sir.entities.Date(parseToDate(dates.getString(i)));
            l.add(d);
            d.setSurvey(survey);
        }

        return l;
    }

    private java.util.Date parseToDate(String dateString) {
        Calendar cal = DatatypeConverter.parseDateTime(dateString);

        return cal.getTime();
    }
}
