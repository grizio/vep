const user = 'user';
const userManager = 'user-manager';
const pageManager = "page-manager";
const theaterManager = "theater-manager";
const companyManager = "company-manager";
const showManager = 'show-manager';
const sessionManager = 'session-manager';

const all = const[
  user,
  userManager,
  pageManager,
  theaterManager,
  companyManager,
  showManager,
  sessionManager
];

const roleI18n = const<String, String>{
  user: 'Utilisateur',
  userManager: 'Gestion des utilisateur',
  pageManager: 'Gestion des pages',
  theaterManager: 'Gestion des théâtres',
  companyManager: 'Gestion des troupes',
  showManager: 'Gestion des spectacles',
  sessionManager: 'Gestion des séances'
};