const user = 'user';
const userManager = 'user-manager';
const pageManager = "page-manager";
const theaterManager = "theater-manager";
const companyManager = "company-manager";

const all = const[
  user,
  userManager,
  pageManager,
  theaterManager,
  companyManager
];

const roleI18n = const<String, String>{
  user: 'Utilisateur',
  userManager: 'Gestion des utilisateur',
  pageManager: 'Gestion des pages',
  theaterManager: 'Gestion des théâtres',
  companyManager: 'Gestion des troupes'
};