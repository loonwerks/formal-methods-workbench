#!/usr/local/bin/python3
# encoding: utf-8
'''
Deploy releases of the Formal Methods Workbench update site

This module uses the Github to deploy the Formal Methods Workbench plugins
to the update-site project directory.

@copyright:  2019 Collins Aerospace. All rights reserved.
'''

import os
import re
import subprocess
import sys
import tempfile

from argparse import ArgumentParser
from argparse import RawDescriptionHelpFormatter
from git import Repo
from github3 import GitHub
from shutil import copyfile

__all__ = []
__version__ = 0.1
__date__ = '2019-03-29'
__updated__ = '2019-03-29'

AUTH_TOKEN = os.environ['GH_TOKEN'] if 'GH_TOKEN' in os.environ.keys() else None

BASE_PACKAGE = 'com.collins.fmw'
SOURCE_DIR = BASE_PACKAGE
UPDATES_PACKAGE_DIR = '.'.join([BASE_PACKAGE, 'updates'])


class CLIError(Exception):
    '''Generic exception to raise and log different fatal errors.'''

    def __init__(self, msg):
        super(CLIError).__init__(type(self))
        self.msg = "E: %s" % msg

    def __str__(self):
        return self.msg

    def __unicode__(self):
        return self.msg


def package_plugin(plugin_version):
    '''Package a plugin from the exectuables for the corresponding release'''

    gitrepo = Repo(os.getcwd())

    # Since we're in detached head state, make a branch on which to work
    try:
        print('  Creating branch for building %s...' % (plugin_version))
        git_result = gitrepo.git.checkout('-b', plugin_version, with_extended_output=True)
        print(git_result[1])
        if (git_result[0] != 0) :
            sys.stderr.write(git_result[2])
            sys.exit(git_result[0])
    except Exception as e:
        sys.stderr.write(str(e))
        sys.exit(1)

    # Commit/push this repository
    try:
        print('  Adding objects to git index...')
        git_result = gitrepo.git.add('-A', 'update-site', with_extended_output=True)
        print(git_result[1])
        if (git_result[0] != 0) :
            sys.stderr.write(git_result[2])
            sys.exit(git_result[0])
        print('  Calling git commit...')
        git_result = gitrepo.git.commit('-m', '[skip travis-ci] Package plugin %s' % (plugin_version), with_extended_output=True)
        print(git_result[1])
        if (git_result[0] != 0) :
            sys.stderr.write(git_result[2])
            sys.exit(git_result[0])
        print('  Calling git checkout master...')
        git_result = gitrepo.git.checkout('master', with_extended_output=True)
        print(git_result[1])
        if (git_result[0] != 0) :
            sys.stderr.write(git_result[2])
            sys.exit(git_result[0])
        print('  Calling git merge %s...' % (plugin_version))
        git_result = gitrepo.git.merge(plugin_version, '-m', '[skip travis-ci] Merge plugin %s' % (plugin_version), with_extended_output=True)
        print(git_result[1])
        if (git_result[0] != 0) :
            sys.stderr.write(git_result[2])
            sys.exit(git_result[0])
        print('  Calling git push...')
        git_result = gitrepo.git.push('--quiet', '--set-upstream', 'origin-with-token', 'master', with_extended_output=True)
        print(git_result[1])
        if (git_result[0] != 0) :
            sys.stderr.write(git_result[2])
            sys.exit(git_result[0])
        print('  Git update and push complete.')
    except Exception as e:
        sys.stderr.write(str(e))
        sys.exit(1)

DEBUG = 0

def main(argv=None):  # IGNORE:C0111
    '''Command line options.'''

    if argv is None:
        argv = sys.argv
    else:
        sys.argv.extend(argv)

    program_name = os.path.basename(sys.argv[0])
    program_version = "v%s" % __version__
    program_build_date = str(__updated__)
    program_version_message = '%%(prog)s %s (%s)' % (program_version, program_build_date)
    program_shortdesc = __import__('__main__').__doc__.split("\n")[1]
    program_license = '''%s

  Copyright 2019 Collins Aerospace.  All rights reserved.

  BSD 3-Clause
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
  
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.

    * Neither the name of Collins Aerospace nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL COLLINS AEROSPACE BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
''' % (program_shortdesc)

    try:
        # Setup argument parser
        parser = ArgumentParser(description=program_license, formatter_class=RawDescriptionHelpFormatter)
        parser.add_argument("-p", "--plugin_version", dest="plugin_version", help="specify the plugin version (usually tag nane) to be packaged")
        parser.add_argument('-V', '--version', action='version', version=program_version_message)

        # Process arguments
        args = parser.parse_args()

        if AUTH_TOKEN:
            print('Using Auth token string ending %s' % (AUTH_TOKEN[-4:]))
        else:
            print('No AUTH_TOKEN, using unauthenticated access')

        package_plugin(args.plugin_version)

        return 0
    except KeyboardInterrupt:
        ### handle keyboard interrupt ###
        return 0
    except Exception as e:
        if DEBUG:
            raise(e)
        indent = len(program_name) * " "
        sys.stderr.write(program_name + ": " + repr(e) + "\n")
        sys.stderr.write(indent + "  for help use --help")
        return 2


if __name__ == "__main__":
    sys.exit(main())
