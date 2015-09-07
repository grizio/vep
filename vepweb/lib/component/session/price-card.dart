part of vep.component.session;

@Component(
    selector: 'price-card',
    templateUrl: '/packages/vepweb/component/session/price-card.html',
    useShadowDom: false
)
class PriceCardComponent {
  @NgOneWay('price')
  SessionPrice price;
}