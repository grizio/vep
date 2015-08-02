part of vep.model.company;

class Company {
  String canonical;
  String name;
  String address;
  bool isVep;
  String content;

  CompanyForm toCompanyForm() {
    var companyForm = new CompanyForm();
    companyForm.canonical = canonical;
    companyForm.name = name;
    companyForm.address = address;
    companyForm.content = content;
    companyForm.isVep = isVep;
    return companyForm;
  }
}

class CompanyForm {
  @jsonIgnore
  String canonical;
  String name;
  String address;
  bool isVep;
  String content;
}