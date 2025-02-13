class GameCharacter {
  Map<String, String> nato = {
    'citizen': 'شهروند',
    'doctor': 'دکتر',
    'guard': 'نگهبان',
    'detective': 'کاراگاه',
    'commando': 'تکاور',
    'rifleman': 'تفنگدار',
    'nato': 'ناتو',
    'hostage_taker': 'گروگانگیر',
    'godfather': 'پدرخوانده'
  };
  Map<String, String> getCharacter(String scenario, String key) {
    late String image;
    switch (key) {
      case 'citizen':
        image = 'images/citizen.png';
        break;
      case 'doctor':
        image = 'images/doctor.png';
        break;
      case 'detective':
        image = 'images/detective.png';
        break;
      case 'commando':
        image = 'images/commando.png';
        break;
      case 'rifleman':
        image = 'images/rifleman.png';
        break;
      case 'guard':
        image = 'images/guard.png';
        break;
      case 'nato':
        image = 'images/nato.png';
        break;
      case 'hostage_taker':
        image = 'images/hostage_taker.png';
        break;
      case 'godfather':
        image = 'images/godfather.png';
        break;
    }
    return {'name': nato[key]!, 'image': image};
  }
}
