extension NumberSeparator on String {
  String numberSeparator() {
    String priceInText = "";
    int counter = 0;
    for (int i = (length - 1); i >= 0; i--) {
      counter++;
      String str = this[i];
      if ((counter % 3) != 0 && i != 0) {
        priceInText = "$str$priceInText";
      } else if (i == 0) {
        priceInText = "$str$priceInText";
      } else {
        priceInText = ",$str$priceInText";
      }
    }
    return priceInText.trim();
  }
}
