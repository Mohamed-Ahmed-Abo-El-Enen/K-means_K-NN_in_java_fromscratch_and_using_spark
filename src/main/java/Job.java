package Main;

public class Job
{
    private String Title;
    private String Company;
    private String Location;
    private String Type;
    private String Level;
    private String YearsExp;
    private String Country;
    private String Skills;

    Job()
    {
        this.Title = "";
        this.Company = "";
        this.Location = "";
        this.Type = "";
        this.Level = "";
        this.YearsExp = "";
        this.Country = "";
        this.Skills = "";
    }

    Job(String Title, String Company, String Location, String Type, String Level, String YearsExp, String Country,
        String Skills)
    {
        this.Title = Title;
        this.Company = Company;
        this.Location = Location;
        this.Type = Type;
        this.Level = Level;
        this.YearsExp = YearsExp;
        this.Country = Country;
        this.Skills = Skills;
    }

    public String getTitle() {
        return Title;
    }

    public String getCompany() {
        return Company;
    }

    public String getLocation() {
        return Location;
    }

    public String getType() {
        return Type;
    }

    public String getLevel() {
        return Level;
    }

    public String getYearsExp() {
        return YearsExp;
    }

    public String getCountry() {
        return Country;
    }

    public String getSkills() {
        return Skills;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public void setYearsExp(String yearsExp) {
        YearsExp = yearsExp;
    }

    public void setSkills(String skills) {
        Skills = skills;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public void setVariablesByName(String VariableName, String Value) {
        switch (VariableName.toLowerCase()) {
            case "company":
                setCompany(Value);
                break;
            case "location":
                setLocation(Value);
                break;
            case "type":
                setType(Value);
                break;
            case "level":
                setLevel(Value);
                break;
            case "yearsexp":
                setYearsExp(Value);
                break;
            case "country":
                setCountry(Value);
                break;
            case "skills":
                setSkills(Value);
                break;
            default:
                setTitle(Value);
                break;
        }
    }
}