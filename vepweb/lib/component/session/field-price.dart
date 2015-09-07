part of vep.component.session;

@Component(
    selector: 'field-price',
    templateUrl: '/packages/vepweb/component/session/field-price.html',
    useShadowDom: false)
class FieldPriceComponent extends RepeatableContainer<SessionPrice> {
  @override
  SessionPrice createModel() => new SessionPrice();
}