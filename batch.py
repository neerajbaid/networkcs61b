##python3.3
import subprocess
command = ['java Network -q machine random', 'java Network -q random machine' ]


def run_command(command):
  p = subprocess.Popen(command, shell=True,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.STDOUT)
  return iter(p.stdout.readline, b'')
def main():
  counter = 0
  number_of_runs = int(input("Enter the number of runs: "));
  for j in range(number_of_runs):
    index = j % 2
    for i in run_command(command[index]):
      print(i.decode("utf-8").strip("\r\n"))
      if(i == b'>>>> MachinePlayer <<<< WINS!\n'):
        counter += 1
  print("Won " + str(counter) + " games out of "+ str(number_of_runs))

  print(str(((counter/number_of_runs)*100)) + "% win rate")
main()