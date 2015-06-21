part of vep.component.user;

@Component(
    selector: 'user-list-roles',
    templateUrl: '/packages/vepweb/component/user/user-list-roles.html',
    useShadowDom: false)
class UserListRolesComponent extends TableSearchContext {
  final UserService userService;

  @NgTwoWay('processing')
  bool processing;
  Lazy<TableDescriptor> _tableDescriptor = lazy(() => new TableDescriptor([
    new ColumnDescriptor('firstName', 'Prénom', 'text', hasFilter: true),
    new ColumnDescriptor('lastName', 'Nom', 'text', hasFilter: true),
    new ColumnDescriptor('email', 'Adresse e-mail', 'text', hasFilter: true),
    new ColumnDescriptor('role-user', 'Utilisateur', 'checkbox', active: true),
    new ColumnDescriptor('role-user-manager', 'Gestion des utilisateur', 'checkbox', active: true),
    new ColumnDescriptor('role-page-manager', 'Gestion des pages', 'checkbox', active: true)
  ]));

  @override
  TableDescriptor get tableDescriptor => _tableDescriptor.value;

  UserListRolesComponent(this.userService);

  @override
  Future<List<Map<String, Object>>> search(Map<String, Object> filters) {
    return userService.getUserList().then((_){
      if (_.isSuccess) {
        var httpResult = _ as HttpResultSuccessEntity;
        var userList = utils.objectToListOfMap(httpResult.entity);
        userList = userList.map((user){
          for (String role in roles.all) {
            user['role-' + role] = (user['roles'] as List<String>).contains(role);
          }
          return user;
        }).toList();
        return userList;
      } else {
        return [];
      }
    });
  }

  @override
  void onChange(Map<String, Object> data) {
    processing = true;
    List<String> roles = [];
    for (String key in data.keys) {
      if (key.startsWith('role-') && data[key] == true) {
        roles.add(key.substring(5));
      }
    }
    userService.updateRoles(data['uid'], roles).then((_){
      processing = false;
    });
  }
}